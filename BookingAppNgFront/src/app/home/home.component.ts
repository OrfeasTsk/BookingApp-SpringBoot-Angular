import { Component, OnInit } from '@angular/core';
import {DepartmentService} from '../department.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Department} from '../model/department';
import {AuthenticationService} from '../authentication.service';
import {UserDetails} from '../model/user-details';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  loading = false;
  dangerBox = false;
  submitattempt = false;
  locations = [];
  searchForm: any;
  searchFormCopy: any = new Object();
  departments: Department[];
  recommended: Department[];
  filters = false;
  submiterror = '';
  userDetails: UserDetails = new UserDetails();
  currentDate = new Date();

  constructor(private departmentService: DepartmentService, private router: Router, private authenticationService: AuthenticationService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {

    let flag = false;
    this.searchForm = new Object();
    this.searchForm.numberOfAdults = 2;
    this.searchForm.numberOfChildren = 0;
    this.searchForm.roomType = null;


    this.authenticationService.getLoggedInUser().subscribe((userDetails) => {
      Object.assign(this.userDetails, userDetails);

      if(this.hasRole('ROLE_TENANT'))
        this.getRecommended();

      if(this.hasRole('ROLE_HOST')){
        this.searchForm.hostId = this.userDetails.id;
      }
      else
        this.searchForm.hostId = null;


      this.route.queryParams.subscribe((params) => {
        if(Object.keys(params).length !== 0){
          Object.assign(this.searchForm, new Object(params));
          this.searchForm.numberOfAdults = Number(this.searchForm.numberOfAdults);
          this.searchForm.numberOfChildren = Number(this.searchForm.numberOfChildren);
          this.searchForm.startDate = new Date(this.searchForm.startDate);
          this.searchForm.endDate = new Date(this.searchForm.endDate);
          if(this.searchForm.hasInternet === 'true')
            this.searchForm.hasInternet = true;
          if(this.searchForm.hasAirCondition === 'true')
            this.searchForm.hasAirCondition = true;
          if(this.searchForm.hasHeat === 'true')
            this.searchForm.hasHeat = true;
          if(this.searchForm.hasElevator === 'true')
            this.searchForm.hasElevator = true;
          if(this.searchForm.hasLivingRoom === 'true')
            this.searchForm.hasLivingRoom = true;
          if(this.searchForm.hasKitchen === 'true')
            this.searchForm.hasKitchen = true;
          if(this.searchForm.hasTv === 'true')
            this.searchForm.hasTv = true;
          if(this.searchForm.hasParking === 'true')
            this.searchForm.hasParking = true;
          if(this.searchForm.smokingAllowed === 'true')
            this.searchForm.smokingAllowed = true;
          if(this.searchForm.petsAllowed === 'true')
            this.searchForm.petsAllowed = true;
          if(this.searchForm.eventsAllowed === 'true')
            this.searchForm.eventsAllowed = true;
          flag = true;
        }

        if (flag) {
          this.departmentService.search(this.searchForm).subscribe((departments) => {
            Object.assign(this.searchFormCopy, this.searchForm);
            this.departments = departments;
            this.departments.sort((a, b) => (this.findCost(a) - this.findCost(b)));
            this.departments.forEach((department) => {
              department.averageRating = 0;
              department.numberOfReviews = 0;

              department.reviews.forEach((review) => {
                department.numberOfReviews = department.numberOfReviews + 1;
                department.averageRating = department.averageRating + review.stars;
              });

              if(department.numberOfReviews)
                department.averageRating = department.averageRating / department.numberOfReviews;


              department.photos.forEach((photo) => {
                if (photo.name === 'mainPhoto') {
                  department.mainPhoto = photo;
                }
              });
            });
            this.router.navigate(['/homepage/search'],{queryParams: this.searchForm}).then(() => {
              this.filters = true;
            });
          });
        }
      });
    });

  }


  autofill(value: string) {
    if (value) {
      this.departmentService.locationFinder(value).subscribe((locations) => {
        this.locations = locations;

      });
    }
  }

  setDate(value){
    const date = new Date(value);
    if (isNaN(date.getTime())) {
      return null;
    }
    else {
      return date;
    }

  }

  submit(searchform, inputElement) {
    this.currentDate.setHours(0,0,0,0);
    if (searchform.form.valid && this.searchForm.startDate && this.searchForm.endDate && (this.searchForm.endDate.getTime() > this.searchForm.startDate.getTime()) && ( this.searchForm.startDate.getTime() >= this.currentDate.getTime() && this.searchForm.endDate.getTime() >= this.currentDate.getTime())) {
      this.loading = true;
      Object.assign(this.searchFormCopy, this.searchForm);
      this.departmentService.search(this.searchForm).subscribe((departments) => {
        this.loading = false;
        this.submitattempt = false;
        this.dangerBox = false;
        this.departments = departments;
        this.departments.sort((a, b) => (this.findCost(a) - this.findCost(b)));
        this.departments.forEach((department) => {
          department.averageRating = 0;
          department.numberOfReviews = 0;

          department.reviews.forEach((review) => {
            department.numberOfReviews = department.numberOfReviews + 1;
            department.averageRating = department.averageRating + review.stars;
          });

          if(department.numberOfReviews)
            department.averageRating = department.averageRating / department.numberOfReviews;

            department.photos.forEach((photo) => {
              if (photo.name === 'mainPhoto') {
                department.mainPhoto = photo;
              }
            });
          });
        this.router.navigate(['/homepage/search'],{queryParams: this.searchForm}).then(() => {
          this.filters = true;
        });
      },
      error => {
        this.loading = false;
        this.submiterror = error.error.message;
        this.dangerBox = true;
        this.submitattempt = true;
      }
      );
    }
    else{
      this.submitattempt = true;
      this.dangerBox = true;


      if (!inputElement.value) {
        this.submiterror = 'Choose your destination';
      }
      else if (!this.searchForm.startDate) {
        this.submiterror = 'Insert the check in date';
 }
      else if (!this.searchForm.endDate) {
        this.submiterror = 'Insert the check out date';
      }
      else if (this.searchForm.endDate.getTime() <= this.searchForm.startDate.getTime()) {
        this.submiterror = 'Check out date must be after check in date';
      }
      else if(this.searchForm.startDate.getTime() < this.currentDate.getTime() || this.searchForm.endDate.getTime() < this.currentDate.getTime() ){
        this.submiterror = 'Invalid dates';
      }

    }
  }


  hasRole(rolename: string): boolean{
    let flag = false;
    if(this.userDetails) {
      this.userDetails.roles.forEach((role) => {
        if (role === rolename)
          flag = true;
      });
    }
    return flag;
  }



  getRecommended(){
    this.departmentService.getRecommended().subscribe((departments) => {
      this.recommended = departments;
    });

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

}
