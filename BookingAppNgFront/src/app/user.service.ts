import { Injectable } from '@angular/core';
import { User } from './model/user';
import { Observable } from 'rxjs';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json', Accept: 'application/json' })
};

@Injectable()
export class UserService {

  constructor(private http: HttpClient) {}


  getUsers(): Observable<User[]> {

    return this.http.get<User[]>('https://localhost:8443/users/');
  }

  getUser(id: string): Observable<User> {
    return this.http.get<User>('https://localhost:8443/users/' + id);
  }

  changeProfilePhoto(id: number , photo: FormData): Observable<HttpResponse<any>>{

    return this.http.put<any>('https://localhost:8443/users/' + id.toString() + '/profilephoto' , photo, {observe : 'response'});

  }

  changePassword(id: string , pwdDetails: any): Observable<HttpResponse<any>>{

    return this.http.put<any>('https://localhost:8443/users/' + id + '/passwordchange' , pwdDetails, {observe : 'response'});

  }

  editUser(user: User): Observable<HttpResponse<any>>{

    return this.http.put<any>('https://localhost:8443/users/' + user.id.toString() + '/edit', user, {observe : 'response'});

  }

  accepthost(uid: number): Observable<HttpResponse<any>> {
    return this.http.put<any>('https://localhost:8443/admin/accept/users/' + uid.toString() , {observe : 'response'});
  }


}
