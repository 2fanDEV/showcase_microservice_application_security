export interface BlogArticle {
    id?: number;
    username : String;
    text : String;
    heading: String;
    lastEditedTimeStamp? : Date;
    creationTimeStamp? : Date;
    likes? : number;
    amountOfComments? : number;
}
