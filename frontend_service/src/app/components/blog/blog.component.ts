import { DatePipe, LocationChangeListener } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { lastValueFrom, Observable } from 'rxjs';
import { User } from 'src/app/services/user-service/model/user';
import { UserService } from 'src/app/services/user-service/user.service';
import { environment } from 'src/environments/environment';
import { BlogArticle } from './model/blog-article';
import { LikeService } from 'src/app/services/like-service/like.service';
import { LoginService } from 'src/app/services/loginService/login.service';
import { AccessTokenService } from 'src/app/services/access-token-service/access-token.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-blog',
  templateUrl: './blog.component.html',
  styleUrls: ['./blog.component.css'],
})
export class BlogComponent implements OnInit, OnChanges, OnDestroy {
  constructor(
    private httpClient: HttpClient,
    private userService: UserService,
    private datePipe: DatePipe,
    private likeService: LikeService,
    private loginService: LoginService,
    private accessTokenService: AccessTokenService,
    private router: Router
  ) {
    this.currentUser = this.userService.getCurrentUser();
  }

  @Input()
  blogFeed: boolean = true;
  @Input()
  blogUsername: boolean = false;
  @Input()
  blogUserLikes: boolean = false;
  @Input()
  blogUserComments: boolean = false;

  blogArticleToBeCreated: BlogArticle;
  allBlogs: BlogArticle[] = [];
  currentUser: User;
  currentHeading: String;
  currentText: String;
  collapsedCommentsForBlog: number[] = [];
  somethingVisible: boolean = false;
  allBlogLikesByUser: number[];

  allBlogLikesCounter: Map<string, number> = new Map();
  allBlogCommentCounter: Map<string, number> = new Map();

  async ngOnInit() {
    if (this.blogFeed) await this.getBlogFeed(); //normal feed
  }

  async ngOnChanges(changes: SimpleChanges) {
    if (this.blogFeed) {
      this.allBlogs = [];
      await this.getBlogFeed(); //normal feed
    } else if (this.blogUsername && !this.blogUserLikes && !this.blogFeed) {
      this.allBlogs = [];
      await this.getBlogUserName(); // profile view for current user in url path
    } else if (!this.blogUsername && this.blogUserLikes && !this.blogFeed) {
      this.allBlogs = [];
      await this.getBlogsLikedByUserName();
    } else if (
      !this.blogUserLikes &&
      !this.blogUsername &&
      this.blogUserComments &&
      !this.blogFeed
    ) {
      this.allBlogs = [];
      await this.getBlogsUserCommented();
    }
  }

  ngOnDestroy() {
    this.allBlogs = [];
  }

  /**
   * Get all blogs a user commented on.
   */
  async getBlogsUserCommented() {
    if (this.userService.isLoggedIn()) {
      await this.userService.getUser().then((user) => {
        this.currentUser = user;
      });
      this.getAllLikes();
    }

    let username: String = this.router.url.replace('/', '');
    setTimeout(() => {
      this.getAllBlogsUserCommentedOn(username).subscribe({
        next: (res) => {
          setTimeout(async () => {
            console.log(res);
            this.allBlogs = res.sort((a, b) => b.id! - a.id!);
            this.dateConversion(this.allBlogs);
            let allBlogIds: number[] = [];
            this.allBlogs.forEach((elem) => allBlogIds.push(elem.id!));
            await this.likeService
              .getLikeCounterForCommentIds(allBlogIds)
              .then((mapWithEntries: Object) => {
                console.log(mapWithEntries);
                this.allBlogLikesCounter = new Map(
                  Object.entries(mapWithEntries)
                );
              });
          }, 150);
        },
        error: (err) => {
          console.log(err);
        },
      });
    }, 200);
  }

  /**
   * Get all blogs liked by a specific username
   */
  async getBlogsLikedByUserName() {
    if (this.userService.isLoggedIn()) {
      await this.userService.getUser().then((user) => {
        this.currentUser = user;
      });
      this.getAllLikes();
    }

    let username: String = this.router.url.replace('/', '');
    setTimeout(() => {
      //
      //
      this.getAllBlogsLikedByUserName(username).subscribe({
        next: (res) => {
          this.getAllBlogsById(res).then((resolution) => {
            setTimeout(async () => {
              console.log(res);
              this.allBlogs = resolution.sort((a, b) => b.id! - a.id!);
              this.dateConversion(this.allBlogs);
              let allBlogIds: number[] = [];
              this.allBlogs.forEach((elem) => allBlogIds.push(elem.id!));
              await this.likeService
                .getLikeCounterForBlogIds(allBlogIds)
                .then((mapWithEntries: Object) => {
                  console.log(mapWithEntries);
                  this.allBlogLikesCounter = new Map(
                    Object.entries(mapWithEntries)
                  );
                });
            }, 150);
          });
        },
        error: (err) => {
          console.log(err);
        },
      });
    }, 200);
  }

  /**
   * get all the blogs by username and their likes
   */
  async getBlogUserName() {
    if (this.userService.isLoggedIn()) {
      await this.userService.getUser().then((user) => {
        this.currentUser = user;
      });
      this.getAllLikes();
    }

    let username: String = this.router.url.replace('/', '');
    setTimeout(() => {
      this.getAllBlogsByUserName(username).subscribe({
        next: (res) => {
          setTimeout(async () => {
            this.allBlogs = res.sort((a, b) => b.id! - a.id!);
            this.dateConversion(this.allBlogs);
            let allBlogIds: number[] = [];
            this.allBlogs.forEach((elem) => allBlogIds.push(elem.id!));
            await this.likeService
              .getLikeCounterForBlogIds(allBlogIds)
              .then((mapWithEntries: Object) => {
                console.log(mapWithEntries);
                this.allBlogLikesCounter = new Map(
                  Object.entries(mapWithEntries)
                );
              });
          }, 150);
        },
        error: (err) => {
          console.log(err);
        },
      });
    }, 200);
  }

  async getBlogFeed() {
    if (this.userService.isLoggedIn()) {
      await this.userService.getUser().then((user) => {
        this.currentUser = user;
      });
      this.getAllLikes();
    }

    setTimeout(() => {
      this.getAllBlogs().subscribe({
        next: (res) => {
          setTimeout(async () => {
            this.allBlogs = res.sort((a, b) => b.id! - a.id!);
            this.dateConversion(this.allBlogs);
            let allBlogIds: number[] = [];
            this.allBlogs.forEach((elem) => allBlogIds.push(elem.id!));
            await this.likeService
              .getLikeCounterForBlogIds(allBlogIds)
              .then((mapWithEntries: Object) => {
                console.log(mapWithEntries);
                this.allBlogLikesCounter = new Map(
                  Object.entries(mapWithEntries)
                );
              });
            await this.getCommentAmount(allBlogIds).then(
              (mapWithEntries: Object) => {
                console.log('commentSize', mapWithEntries);
                this.allBlogCommentCounter = new Map(
                  Object.entries(mapWithEntries)
                );
              }
            );
          }, 150);
        },
        error: (err) => {
          console.log(err);
        },
      });
    }, 200);
  }

  getAllLikes() {
    this.likeService.getAllBlogLikesForUserName(this.currentUser.userName).then(
      (res) => {
        this.allBlogLikesByUser = res;
      },
      (err) => {
        console.log(err);
      }
    );
  }

  sendBlog(): void {
    if(!this.userService.isLoggedIn())
      this.loginService.login();

    this.currentUser = this.userService.getCurrentUser();

    this.blogArticleToBeCreated = {
      username: this.currentUser.userName,
      heading: this.currentHeading,
      text: this.currentText,
    };

    const form = new FormData();

    form.append('blogArticle', JSON.stringify(this.blogArticleToBeCreated));
    console.log(form);
    this.allBlogs = [];
    this.httpClient
      .post(environment.url.api.baseIP + '/blog-service/createBlog', form)
      .subscribe({
        next: async (res) => {
          if (this.blogFeed) {
            this.getAllBlogs().subscribe({
              next: async (res) => {
                this.allBlogs = res.sort((a, b) => b.id! - a.id!);
                let allBlogIds: number[] = [];
                this.allBlogs.forEach((elem) => allBlogIds.push(elem.id!));
                await this.likeService
                  .getLikeCounterForBlogIds(allBlogIds)
                  .then((mapWithEntries: Object) => {
                    this.allBlogLikesCounter = new Map(
                      Object.entries(mapWithEntries)
                    );
                  });
                this.dateConversion(this.allBlogs);
              },
              error: (err) => {
                console.log(err);
              },
            });
          } else if (this.blogUsername) await this.getBlogUserName();
        },
        error: (err) => {
          console.log(err);
        },
      });
  }

  deleteBlog(id: number) {
    if (this.userService.currentUser == null || undefined) {
      this.loginService.login();
    }
    let tempBlogs = this.allBlogs;
    this.allBlogs = [];

    this.httpClient
      .post(
        environment.url.api.baseIP + '/blog-service/deleteBlogArticle/' + id,
        this.userService.currentUser.userName
      )
      .subscribe({
        next: async (res) => {
          if (this.blogFeed) {
            this.getAllBlogs().subscribe({
              next: async (res) => {
                this.allBlogs = res.sort((a, b) => b.id! - a.id!);
                this.dateConversion(this.allBlogs);
                let allBlogIds: number[] = [];
                this.allBlogs.forEach((elem) => allBlogIds.push(elem.id!));
                await this.likeService
                  .getLikeCounterForBlogIds(allBlogIds)
                  .then((mapWithEntries: Object) => {
                    this.allBlogLikesCounter = new Map(
                      Object.entries(mapWithEntries)
                    );
                  });
                await this.getCommentAmount(allBlogIds).then(
                  (mapWithEntries: Object) => {
                    console.log(mapWithEntries);
                    Object.entries(mapWithEntries);
                  }
                );
              },
              error: (err) => {
                console.log(err);
                this.allBlogs = tempBlogs;
              },
            });
          } else if (this.blogUsername) await this.getBlogUserName();
        },
        error: (err) => {
          console.log(err);
        },
      });
  }

  public checkIfLiked(blogId: number): boolean {
    if (this.userService.isLoggedIn()) {
      for (let i = 0; i < this.allBlogLikesByUser.length; i++) {
        if (this.allBlogLikesByUser[i] == blogId) return true;
      }
    }
    return false;
  }

  public toggleLikeBlog(blogId: number, authorName: String): void {
    if (this.userService.isLoggedIn()) {
      console.log(blogId);
      let tempLikedBlogId: number = blogId;
      let isAlreadyLiked: boolean = false;
      if (this.allBlogLikesByUser.length != 0) {
        this.allBlogLikesByUser.some((elem) => {
          if (elem == blogId) {
            isAlreadyLiked = true;
            let index = this.allBlogLikesByUser.indexOf(elem);
            this.allBlogLikesByUser.splice(index, 1);
            this.allBlogs!.some((blog) => {
              if (blog.id == blogId)
                this.allBlogLikesCounter.set(
                  blog.id.toString(),
                  this.allBlogLikesCounter.get(blog.id.toString())! - 1
                );
            });
          }
        });
        if (!isAlreadyLiked) {
          this.allBlogLikesByUser.push(tempLikedBlogId);
          this.allBlogs!.some((blog) => {
            if (blog.id == blogId)
              this.allBlogLikesCounter.set(
                blog.id.toString(),
                this.allBlogLikesCounter.get(blog.id.toString())! + 1
              );
          });
        }
      } else {
        this.allBlogLikesByUser.push(tempLikedBlogId);
      }
      this.likeService.likeBlog(tempLikedBlogId, authorName);
    } else {
      this.loginService.login();
    }
  }

  public like(blogId: number, authorName: String): void {
    if(this.userService.isLoggedIn())
    this.likeService.likeBlog(blogId, authorName);
  }
  

  public dateConversion(blogs: BlogArticle[]) {
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

  toggleCollapsedCommentsForBlog(blogId: number) {
    let index = this.collapsedCommentsForBlog.indexOf(blogId);
    if (index == -1) {
      this.collapsedCommentsForBlog.push(blogId);
      this.somethingVisible = !this.somethingVisible;
    } else {
      this.collapsedCommentsForBlog.splice(index, 1);
      this.somethingVisible = !this.somethingVisible;
    }
  }

  navigateToProfile(username: String) {
    this.router.navigateByUrl('/' + username);
  }

  getAllBlogs(): Observable<BlogArticle[]> {
    return this.httpClient.get<BlogArticle[]>(
      environment.url.api.baseIP + '/blog-service/getAllBlogArticles'
    );
  }

  getCommentAmount(blogIds: number[]): Promise<any> {
    return lastValueFrom(
      this.httpClient.post<any>(
        environment.url.api.baseIP + '/comment-service/getCommentSizeByBlogId',
        blogIds
      )
    );
  }

  getAllBlogsById(blogIds: number[]): Promise<BlogArticle[]> {
    return lastValueFrom(
      this.httpClient.post<BlogArticle[]>(
        environment.url.api.baseIP +
          '/blog-service/getAllBlogArticlesById', blogIds
      )
    );
  }

  getAllBlogsByUserName(username: String): Observable<BlogArticle[]> {
    return this.httpClient.get<BlogArticle[]>(
      environment.url.api.baseIP +
        '/blog-service/getAllBlogArticlesByUserName?username=' +
        username
    );
  }

  getAllBlogsLikedByUserName(username: String): Observable<number[]> {
    return this.httpClient.get<number[]>(
      environment.url.api.baseIP +
        '/statistics-service/blog/likesForBlogsByUserName?username=' +
        username
    );
  }

  getAllBlogsUserCommentedOn(username: String): Observable<BlogArticle[]> {
    return this.httpClient.get<BlogArticle[]>(
      environment.url.api.baseIP +
        '/blog-service/getAllBlogsUserCommentedOn?username=' +
        username
    );
  }
}
