import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {Department} from './model/department';
import {UserDetails} from './model/user-details';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json', Accept: 'application/json' })
};



@Injectable({
  providedIn: 'root'
})
export class DepartmentService {


  constructor(private http: HttpClient) {}

  addDepartment(formWrapper: FormData): Observable<HttpResponse<string>>{
    return this.http.post<string>('https://localhost:8443/departments/departmentregister', formWrapper, {observe : 'response'});
  }

  getDepartments(): Observable<Department[]>{
    return this.http.get<Department[]>('https://localhost:8443/host/departments');
  }

  getDepartment(id: string): Observable<Department>{
    return this.http.get<Department>('https://localhost:8443/departments/' + id);
  }

  deletePhoto(depId: number , photoId: number): Observable<HttpResponse<any>>{

    return this.http.delete<any>('https://localhost:8443/departments/' + depId.toString() + '/photos/' + photoId.toString(), {observe : 'response'});

  }

  addPhotos(id: number , photos: FormData): Observable<HttpResponse<any>>{

    return this.http.put<any>('https://localhost:8443/departments/' + id.toString() + '/photos' , photos, {observe : 'response'});
  }

  changeMainPhoto(id: number , photo: FormData): Observable<HttpResponse<any>>{

    return this.http.put<any>('https://localhost:8443/departments/' + id.toString() + '/photos/mainphoto' , photo, {observe : 'response'});

  }

  editDepartment(department: Department): Observable<HttpResponse<any>>{

    return this.http.put<any>('https://localhost:8443/departments/' + department.id.toString() , department, {observe : 'response'});

  }

  locationFinder(value: string): Observable<string[]>{

    return this.http.get<string[]>('https://localhost:8443/departments/finder?location=' + value );

  }

  search(searchForm: any): Observable<Department[]>{
    const submitForm = new Object();
    let queryStr = '?';
    Object.assign(submitForm, searchForm);

    queryStr = queryStr + 'location=' + searchForm.place;
    queryStr = queryStr + '&startDate=' + searchForm.startDate.toISOString().slice(0, 10);
    queryStr = queryStr + '&endDate=' + searchForm.endDate.toISOString().slice(0, 10);
    queryStr = queryStr + '&numberOfPeople=' + (searchForm.numberOfAdults + searchForm.numberOfChildren).toString();
    if (searchForm.hostId) {
      queryStr = queryStr + '&hostId=' + searchForm.hostId;
    }
    if (searchForm.roomType) {
      queryStr = queryStr + '&roomType=' + searchForm.roomType;
    }
    if (searchForm.minCost) {
      queryStr = queryStr + '&minCost=' + searchForm.minCost.toString();
    }
    if (searchForm.maxCost) {
      queryStr = queryStr + '&maxCost=' + searchForm.maxCost.toString();
    }
    if (searchForm.smokingAllowed) {
      queryStr = queryStr + '&smokingAllowed=true';
    }
    if (searchForm.petsAllowed) {
      queryStr = queryStr + '&petsAllowed=true';
    }
    if (searchForm.eventsAllowed) {
      queryStr = queryStr + '&eventsAllowed=true';
    }
    if (searchForm.hasInternet) {
      queryStr = queryStr + '&hasInternet=true';
    }
    if (searchForm.hasLivingRoom) {
      queryStr = queryStr + '&hasLivingRoom=true';
    }
    if (searchForm.hasAirCondition) {
      queryStr = queryStr + '&hasAirCondition=true';
    }
    if (searchForm.hasHeat) {
      queryStr = queryStr + '&hasHeat=true';
    }
    if (searchForm.hasKitchen) {
      queryStr = queryStr + '&hasKitchen=true';
    }
    if (searchForm.hasTv) {
      queryStr = queryStr + '&hasTv=true';
    }
    if (searchForm.hasParking) {
      queryStr = queryStr + '&hasParking=true';
    }
    if (searchForm.hasElevator) {
      queryStr = queryStr + '&hasElevator=true';
    }


    return this.http.get<Department[]>('https://localhost:8443/departments/search' + queryStr);

  }


  getDepartmentsForExport(type: string): Observable<any>{
    if(type === 'json')
      return this.http.get('https://localhost:8443/departments/export', {headers: new HttpHeaders({ 'Content-Type': 'application/json', Accept: 'application/json' })});
    else if(type === 'xml')
      return this.http.get('https://localhost:8443/departments/export', {headers: new HttpHeaders({'Content-Type': 'application/xml', Accept: 'application/xml' }), responseType: 'text'});


  }

  getRecommended(): Observable<Department[]>{
    return this.http.get<Department[]>('https://localhost:8443/departments/recommended');
  }

}
