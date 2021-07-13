import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Department} from '../../model/department';
import {DepartmentService} from '../../department.service';
import {DomSanitizer} from '@angular/platform-browser';
import {Router} from '@angular/router';

@Component({
  selector: 'app-department-search',
  templateUrl: './department-search.component.html',
  styleUrls: ['./department-search.component.css']
})
export class DepartmentSearchComponent implements OnInit {

  @Input() departments: Department[];
  @Input() searchForm: any;
  stars = 3.8;
  page = 1;

  constructor(private departmentService: DepartmentService, private domSanitizer: DomSanitizer, private router: Router) { }

  ngOnInit(): void {


  }


  displayMainPhoto(department: Department): any{

    if(department.mainPhoto.type === 'image/png')
      return this.domSanitizer.bypassSecurityTrustUrl('data:image/png;base64,' + department.mainPhoto.photoBytes);
    else if(department.mainPhoto.type === 'image/jpeg')
      return this.domSanitizer.bypassSecurityTrustUrl('data:image/jpeg;base64,' + department.mainPhoto.photoBytes);

    return null;
  }

  Type(department: Department): string{
    if(department.type === 'privateRoom')
      return 'Private Room';
    else if(department.type ==='publicRoom')
      return 'Public Room';
    else
      return 'Apartment';

  }

  findCost(department): number {
    let cost = department.costPerDay;
    if( this.searchForm.numberOfChildren || this.searchForm.numberOfAdults)
      if(this.searchForm.numberOfChildren + this.searchForm.numberOfAdults <= department.maxPeople)
        cost = cost + (this.searchForm.numberOfAdults + this.searchForm.numberOfChildren) * department.costPerPerson;
      else
        cost = cost + department.maxPeople * department.costPerPerson;
    return cost;
  }



  redirectToDetails(department: Department){
    let params: any = new Object();
    params.startDate = this.searchForm.startDate;
    params.endDate = this.searchForm.endDate;
    params.numberOfChildren = this.searchForm.numberOfChildren;
    params.numberOfAdults = this.searchForm.numberOfAdults;
    params.booked = false;
    this.router.navigate(['/departments/', department.id.toString()], {queryParams: params});
  }



}
