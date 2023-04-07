import { HttpClient } from '@angular/common/http';
import { Injectable, KeyValueDiffers } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Like } from './model/like';
import {firstValueFrom, lastValueFrom, map} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class LikeService {

  constructor(private httpClient : HttpClient) { }
  private baseURL : string = environment.url.api.baseIP;
  private statisticService : string = "/statistics-service";
  private blog = "/blog";
  private comment = "/comment";

  /**
   * Get all the liked blogs for a username
   * @param username 
   * @returns 
   */
  getAllBlogLikesForUserName(username: String) : Promise<number[]> {
    return lastValueFrom(this.httpClient
            .get<number[]>(this.baseURL + this.statisticService + this.blog + "/likesForBlogsByUserName?username=" + username));
  }

  /**
   * get all the liked amount for each blog
   * @param blogIds 
   * @returns 
   */
  getLikeCounterForBlogIds(blogIds: number[]) : Promise<any>
  {
     return lastValueFrom(this.httpClient.post<any>(this.baseURL + this.statisticService + this.blog + "/getAllBlogLikeCounter", blogIds));

  }

  /**
   * the same just for all the comments
   * @param commentIds 
   * @returns 
   */
  getLikeCounterForCommentIds(commentIds: number[]) : Promise<any>
  {
    return lastValueFrom(this.httpClient.post<any>(this.baseURL + this.statisticService + this.comment + "/getAllCommentLikeCounter", commentIds));
  }

  /**
   * get all liked comments for a user
   * @param username 
   * @returns 
   */
  getAllCommentLikesForUserName(username: String) : Promise<number[]> {
    return lastValueFrom(this.httpClient
      .get<number[]>(this.baseURL + this.statisticService + this.comment + "/likesForCommentsByUserName?username=" + username));
  }

  /**
   * Like a specific blog by their username
   * @param likeBlogId 
   * @param authorName 
   */
  likeBlog(likeBlogId : number, authorName: String)
  {
    let blogId = likeBlogId;
    this.httpClient.post(this.baseURL + this.statisticService + this.blog + "/likeBlog?authorName=" + authorName + "&blogId=" + blogId, null)
        .subscribe({
          next: (res) => { console.log("RES: " + res); },
          error: (err) => { console.log("ERR: " + err); }
        })
  }

  /**
   * like a comment by their specific commentId, authorName and blogId
   * @param id 
   * @param authorName 
   * @param blogId 
   */
  likeComment(id: number, authorName: String, blogId: number)
  {
    /**
     * If user is not logged in and is not authorized,
     */
    let commentId: number = id;
    this.httpClient.post(this.baseURL + this.statisticService + this.comment + "/likeComment?authorName=" + authorName + "&commentId=" + commentId + "&blogId=" + blogId, null)
        .subscribe({
          next: (res) => { console.log("RES: " + res); },
          error: (err) => { console.log("ERR: " + err); }
    })
  }
}
