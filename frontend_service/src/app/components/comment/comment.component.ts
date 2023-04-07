import { DatePipe } from '@angular/common';
import { HttpClient, HttpResponse } from '@angular/common/http';
import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChange,
  SimpleChanges,
} from '@angular/core';
import { LikeService } from 'src/app/services/like-service/like.service';
import { Like } from 'src/app/services/like-service/model/like';
import { LoginService } from 'src/app/services/loginService/login.service';
import { User } from 'src/app/services/user-service/model/user';
import { UserService } from 'src/app/services/user-service/user.service';
import { environment } from 'src/environments/environment';
import { BlogArticle } from '../blog/model/blog-article';
import { BlogComment } from './model/blog-comment';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
})
export class CommentComponent implements OnInit, OnChanges {
  
  @Input()
  collapsedComments: number[];
  @Input()
  blog: BlogArticle;
  @Input()
  somethingVisible: boolean;

  currentText: String;

  allComments: BlogComment[] = [];

  private baseIP: String = environment.url.api.baseIP;

  currentUser: User = this.userService.getCurrentUser();

  allCommentLikesByUser: number[];

  commentLikeAmount: number;

  allCommentsLikeCounter: Map<String, number> = new Map();

  constructor(
    private userService: UserService,
    private loginService: LoginService,
    private httpClient: HttpClient,
    private datePipe: DatePipe,
    private likeService: LikeService
  ) {}

  ngOnInit(): void {}

  /**
   * Get all liked comments by a user 
   * so we can display if something is already liked or not
   */
  getAllLikesByUser() {
    if (this.userService.isLoggedIn()) {
      this.likeService
        .getAllCommentLikesForUserName(this.currentUser.userName)
        .then(
          async (res: number[]) => {
            this.allCommentLikesByUser = res;
            this.dateConversion(this.allComments.filter((comment : BlogComment) => this.allCommentLikesByUser.includes(comment.id!)));
            let allCommentIds: number[] = [];
            this.allCommentLikesByUser.forEach((elem) => allCommentIds.push(elem));
            await this.likeService
            .getLikeCounterForCommentIds(allCommentIds)
            .then((mapWithEntries: Object) => {
              console.log(mapWithEntries);
              this.allCommentsLikeCounter = new Map(
                Object.entries(mapWithEntries)
              );
            });
          },
          (err) => {
            console.log(err);
          }
        );
    }
  }

  /**
   * For example if we write a new comment
   * then an event is triggered which calls this function
   * so we get all comments for a specific blog again to refresh the page
   * @param changes 
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (this.collapsedComments.includes(this.blog.id!)) {
      this.getAllCommentsForBlogId(this.blog.id!).subscribe({
        next: (res) => {
          setTimeout(async () => {
            this.allComments = res;
            this.dateConversion(this.allComments);
            let allCommentIds: number[] = [];
            this.allComments.forEach((elem) => allCommentIds.push(elem.id!));
            await this.likeService
            .getLikeCounterForCommentIds(allCommentIds)
            .then((mapWithEntries: Object) => {
              this.allCommentsLikeCounter = new Map(
                Object.entries(mapWithEntries)
              );
            });
          }, 250);
        },
        error: (err) => {
          console.log(err);
        },
      });

      this.getAllLikesByUser();
    }
  }

  /**
   * get all comments for a blogId
   * @param blogId 
   * @returns 
   */
  getAllCommentsForBlogId(blogId: number) {
    return this.httpClient.get<BlogComment[]>(
      this.baseIP + '/comment-service/getCommentsByBlogId/' + blogId
    );
  }
  
  /**
   * Convert date to a more readable string
   * did not work as intended as now it is displaying 
   * it correctly the hours are in the 1-12hr format 
   * @param blogs 
   */
  public dateConversion(blogs: BlogComment[]) {
    blogs.forEach((blogArticle) => {
      if (blogArticle.creationTimeStamp) {
        let date: Date = blogArticle.creationTimeStamp;
        let dateString = this.datePipe
          .transform(date, 'yyyy-MM-dd hh:mm')
          ?.toString();
        if (dateString != null) date = new Date(dateString);
        blogArticle.creationTimeStamp = date;
      }
    });
  }

  /**
   * Sending a comment to a specific blog
   * if not logged in and authorized -> reroute to authorization endpoint for oauth2 flow
   */
  sendComment(): void {
    if(!this.userService.isLoggedIn())
      this.loginService.login();

    if (this.currentText != null) {
      let blogComment: BlogComment = {
        blogId: this.blog.id!,
        username: this.currentUser.userName,
        text: this.currentText,
      };

      this.httpClient
        .post(
          environment.url.api.baseIP + '/comment-service/createComment',
          blogComment
        )
        .subscribe({
          complete: () => {
            this.blog.amountOfComments!++,
              this.getAllCommentsForBlogId(this.blog.id!).subscribe({
                next: (res: any) => {
                  this.allComments = res;
                  this.dateConversion(this.allComments);
                },
                error: (err) => {
                  console.log(err);
                },
              });
          },
          next: (res: any) => {
            console.log(res);
          },
          error: (err) => {
            console.log(err);
          },
        });
    }
  }

  /**
   * if user is logged in, check if we liked a specific blog
   * @param commentId 
   * @returns 
   */
  public checkIfLiked(commentId: number): boolean {
    if (this.userService.isLoggedIn()) {
      for (let i = 0; i < this.allCommentLikesByUser.length; i++) {
        if (this.allCommentLikesByUser[i] == commentId) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * if already liked, dislike comment
   * if not liked, like comment
   * 
   * reroute to login page for authentication 
   * and to get a token, if not loggedin
   * @param commentId 
   * @param authorName 
   * @param blogId 
   */
  public toggleLikeComment(commentId: number, authorName: String, blogId: number): void {
    if (this.userService.isLoggedIn()) {
      let tempLike: number = commentId;
      let isAlreadyLiked: boolean = false;
      if (this.allCommentLikesByUser.length != 0) {
        this.allCommentLikesByUser.some((elem) => {
          if (elem == commentId) {
            isAlreadyLiked = true;
            let index = this.allCommentLikesByUser.indexOf(elem);
            this.allCommentLikesByUser.splice(index, 1);
            this.allComments.some((comment) => {
              if (comment.id == commentId) this.allCommentsLikeCounter.set(comment.id.toString(), this.allCommentsLikeCounter.get(comment.id.toString())! - 1);
            });
          }
        });
        if (!isAlreadyLiked) {
          this.allCommentLikesByUser.push(tempLike);
          this.allComments.some((comment) => {
            if (comment.id == commentId) this.allCommentsLikeCounter.set(comment.id.toString(), this.allCommentsLikeCounter.get(comment.id.toString())! + 1);
          });
        }
      } else {
        this.allCommentLikesByUser.push(tempLike);
      }
      this.likeService.likeComment(tempLike, authorName, blogId);
    } else {
      this.loginService.login();
    }
  }
}
