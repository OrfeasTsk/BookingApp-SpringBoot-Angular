import {Observable} from 'rxjs';
import {Message} from './model/message';
import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private http: HttpClient) { }

  message(userId: number, depId: number , messId: number , message: Message): Observable<HttpResponse<any>>{
    let queryStr = 'https://localhost:8443/users/' + userId.toString() + '/messages?depId=' + depId.toString();
    if(messId){
      queryStr = queryStr + '&messId=' + messId.toString();
    }

    return this.http.post<any>( queryStr , message , {observe : 'response'});
  }

  getReceivedMessages(userId: number, depId: number): Observable<Message[]>{
    let queryStr = 'https://localhost:8443/users/' + userId.toString() + '/messages/received';
    if(depId){
      queryStr = queryStr + '?depId=' + depId.toString();
    }
    return this.http.get<Message[]>(queryStr);
  }

  getSentMessages(userId: number, depId: number): Observable<Message[]>{
    let queryStr = 'https://localhost:8443/users/' + userId.toString() + '/messages/sent';
    if(depId){
      queryStr = queryStr + '?depId=' + depId.toString();
    }
    return this.http.get<Message[]>(queryStr);
  }

  deleteMessage(userId: number, messId: number): Observable<HttpResponse<any>>{
    return this.http.delete<any>('https://localhost:8443/users/' + userId.toString() + '/messages/' + messId.toString(), {observe : 'response'} );
  }


}

