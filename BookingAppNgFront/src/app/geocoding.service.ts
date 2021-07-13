import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpBackend} from '@angular/common/http';
import {Observable} from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json', Accept: 'application/json' })
};


@Injectable({
  providedIn: 'root'
})
export class GeocodingService {

  private http: HttpClient;

  constructor( handler: HttpBackend) {
    this.http = new HttpClient(handler);
  }

  getAddress(long: string , lat: string): Observable<any>{
    return this.http.get<any>('http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/reverseGeocode?f=pjson&langCode=EN&location=' + long + '%2C' + lat);
  }

  setAddress( address: string){

    return this.http.get<any>('http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates?SingleLine=' + encodeURI(address) + '&category=&outFields=*&forStorage=false&f=pjson');

  }

}
