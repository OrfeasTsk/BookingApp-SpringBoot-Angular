import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {Booking} from './model/booking';
import {Observable} from 'rxjs';
import {Review} from './model/review';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  constructor(private http: HttpClient) {}

  reviewDepartment(depId: number, review: Review): Observable<HttpResponse<any>>{
    return this.http.post<any>( 'https://localhost:8443/departments/' + depId.toString() + '/reviews', review , {observe : 'response'});
  }

  reviewHost(hostId: number, review: Review): Observable<HttpResponse<any>>{
    return this.http.post<any>( 'https://localhost:8443/users/' + hostId.toString() + '/reviews', review , {observe : 'response'});
  }

  getDepartmentReviewsForExport(type: string): Observable<any>{
    if(type === 'json')
      return this.http.get('https://localhost:8443/departmentreviews/export', {headers: new HttpHeaders({ 'Content-Type': 'application/json', Accept: 'application/json' })});
    else if(type === 'xml')
      return this.http.get('https://localhost:8443/departmentreviews/export', {headers: new HttpHeaders({'Content-Type': 'application/xml', Accept: 'application/xml' }), responseType: 'text'});
  }

  getHostReviewsForExport(type: string): Observable<any>{
    if(type === 'json')
      return this.http.get('https://localhost:8443/hostreviews/export', {headers: new HttpHeaders({ 'Content-Type': 'application/json', Accept: 'application/json' })});
    else if(type === 'xml')
      return this.http.get('https://localhost:8443/hostreviews/export', {headers: new HttpHeaders({'Content-Type': 'application/xml', Accept: 'application/xml' }), responseType: 'text'});
  }


}
