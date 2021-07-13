import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Booking} from './model/booking';
import {Department} from './model/department';

@Injectable({
  providedIn: 'root'
})
export class BookingService {


  constructor(private http: HttpClient) {}

  book(depId: number, booking: Booking): Observable<HttpResponse<any>>{
   return this.http.post<any>( 'https://localhost:8443/departments/' + depId.toString() + '/bookings', booking , {observe : 'response'});
  }

  getBookings(): Observable<Booking[]>{
    return this.http.get<Booking[]>('https://localhost:8443/bookings/');
  }

  getBooking(id: string): Observable<HttpResponse<Booking>>{
    return this.http.get<Booking>('https://localhost:8443/bookings/' + id, { observe: 'response'});
  }



  cancel(id: number): Observable<HttpResponse<any>>{
    return this.http.delete<any>('https://localhost:8443/bookings/' + id.toString());
  }



  getBookingsForExport(type: string): Observable<any>{
    if(type === 'json')
      return this.http.get('https://localhost:8443/bookings/export', {headers: new HttpHeaders({ 'Content-Type': 'application/json', Accept: 'application/json' })});
    else if(type === 'xml')
      return this.http.get('https://localhost:8443/bookings/export', {headers: new HttpHeaders({'Content-Type': 'application/xml', Accept: 'application/xml' }), responseType: 'text'});
  }



}
