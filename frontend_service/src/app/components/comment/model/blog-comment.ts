export interface BlogComment {
    id?: number;
    text: String;
    username : String;
    creationTimeStamp? : Date;
    editTimeStamp? : Date;
    blogId : number;
    likes? : number;
}
