import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {Department} from '../model/department';
import {DepartmentService} from '../department.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';
import {Photo} from '../model/photo';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import {fromLonLat} from 'ol/proj';
import {Icon, Style} from 'ol/style';
import VectorSource from 'ol/source/Vector';
import {Vector} from 'ol/layer';
import Map from 'ol/Map';
import Tile from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import View from 'ol/View';
import {AuthenticationService} from '../authentication.service';
import {UserDetails} from '../model/user-details';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {BookingService} from '../booking.service';
import {Booking} from '../model/booking';
import {ReviewService} from '../review.service';
import {Review} from '../model/review';
import {Message} from '../model/message';
import {MessageService} from '../message.service';

@Component({
  selector: 'app-department-details',
  templateUrl: './department-details.component.html',
  styleUrls: ['./department-details.component.css']
})
export class DepartmentDetailsComponent implements OnInit {
  department: Department = new Department();
  map: Map;
  marker: Feature;
  deleteButton = false;
  changeButton = false;
  validphotos = true;
  validmainphoto = true;
  userDetails: UserDetails = null;
  params: any = new Object();
  bookModalRef: BsModalRef;
  reviewDepModalRef: BsModalRef;
  reviewHostModalRef: BsModalRef;
  messageHostModalRef: BsModalRef;
  reviewPage = 1;
  hostReviewPage = 1;
  booked = false;
  flag = false;
  reviewDepText = '';
  reviewHostText = '';
  messageHostText = '';
  errormsg = '';
  starsValue = 1;


  @ViewChild('templateRef1') bookModal: TemplateRef<any>;
  @ViewChild('templateRef2') reviewDepModal: TemplateRef<any>;
  @ViewChild('templateRef3') reviewHostModal: TemplateRef<any>;
  @ViewChild('templateRef4') messageHostModal: TemplateRef<any>;


  constructor(private router: Router, private departmentService: DepartmentService, private route: ActivatedRoute, private domSanitizer: DomSanitizer, private authService: AuthenticationService, private bsModalService: BsModalService, private bookingService: BookingService, private reviewService: ReviewService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.authService.getLoggedInUser().subscribe((userDetails) => {
      this.userDetails = userDetails;


    this.route.queryParams.subscribe((params) => {
      if (!params.bookingId)
        this.flag = true;
      if (Object.keys(params).length !== 0){
        Object.assign(this.params, new Object(params));
        if (!this.params.bookingId) {
          this.params.numberOfAdults = Number(this.params.numberOfAdults);
          this.params.numberOfChildren = Number(this.params.numberOfChildren);
          this.params.startDate = new Date(this.params.startDate);
          this.params.endDate = new Date(this.params.endDate);
          if (this.params.startDate.getTime() >= this.params.endDate.getTime()) {
            this.router.navigate(['/homepage']);
          }
        }

      }

    this.departmentService.getDepartment(this.route.snapshot.paramMap.get('id')).subscribe((department) => {
      this.initializeMap(department);
      Object.assign(this.department , department);

      if (this.params.bookingId && this.hasRole('ROLE_TENANT')){
        this.bookingService.getBooking(this.params.bookingId).subscribe((response) => {
          const booking: Booking = response.body;
          if (booking.tenant.id !== this.userDetails.id || booking.department.id !== this.department.id) {
            this.router.navigate(['/homepage/bookings']);
          }
          this.params.startDate = booking.startDate;
          this.params.endDate = booking.endDate;
          this.params.numberOfChildren = booking.numberOfChildren;
          this.params.numberOfAdults =  booking.numberOfAdults;
          this.booked = true;
          this.flag = true;
        },
        error => {
          this.router.navigate(['/homepage/bookings']);
        }
        );

      }



      if (this.params.numberOfAdults || this.params.numberOfChildren) {
        if (this.params.numberOfAdults + this.params.numberOfChildren > this.department.maxPeople) {
          this.router.navigate(['/homepage']);
        }
      }
      this.department.startDate = new Date(department.startDate);
      this.department.endDate = new Date(department.endDate);
      this.department.photos = new Array<Photo>();


      this.department.averageRating = 0;
      this.department.numberOfReviews = 0;

      this.department.reviews.forEach((review) => {
        this.department.numberOfReviews = this.department.numberOfReviews + 1;
        this.department.averageRating = this.department.averageRating + review.stars;
      });

      if (this.department.numberOfReviews) {
        this.department.averageRating = this.department.averageRating / this.department.numberOfReviews;
      }

      department.photos.forEach((photo) => {
          if (photo.name === 'mainPhoto') {
            this.department.mainPhoto = photo;
          }
          else {
            this.department.photos.push(photo);
          }
          });
        },
        error => {

          if( this.hasRole('ROLE_HOST') && this.hasRole('ROLE_TENANT'))
            this.router.navigate(['/choosepage']);
          else if (this.hasRole('ROLE_HOST'))
            this.router.navigate(['/hostpage']);
          else if (this.hasRole('ROLE_ADMIN'))
            this.router.navigate(['/adminpage']);
          else
            this.router.navigate(['/homepage']);

        }
      );
      });
    });
  }


  initializeMap(department: Department){
    this.marker = new Feature({
      geometry: new Point(fromLonLat([department.longitude, department.latitude]))
    });
    this.marker.setStyle(new Style({
      image: new Icon({src: '/assets/marker.png', imgSize: [70 , 70], scale: 0.6 })
    }));
    const vectorSource = new VectorSource({features: [this.marker]});
    const vectorLayer = new Vector({source: vectorSource});
    this.map = new Map({
      target: 'dep_map',
      layers: [
        new Tile({
          source: new OSM()
        }),
        vectorLayer
      ],
      view: new View({
        center: fromLonLat([23.727539, 37.983810]),
        zoom: 3
      })
    });

  }


  getDate(date: Date){
    if (date){
       const str = date.toString().split(' ');
       let output = '';
       output = str[0] + ' ' + str[1] + ' ' + str[2] + ' ' + str[3];
       return output;
    }
  }


  Type(): string{
    if (this.department.type === 'privateRoom') {
      return 'Private Room';
    }
    else if (this.department.type === 'publicRoom') {
      return 'Public Room';
 }
    else {
      return 'Apartment';
 }

  }
  displayPhoto(photo: Photo): any{

    if (photo.type === 'image/png') {
      return this.domSanitizer.bypassSecurityTrustUrl('data:image/png;base64,' + photo.photoBytes);
    }
    else if (photo.type === 'image/jpeg') {
      return this.domSanitizer.bypassSecurityTrustUrl('data:image/jpeg;base64,' + photo.photoBytes);
 }

    return null;
  }


  deletePhoto(photoId: number){
    if (confirm('Are you sure to delete this photo?')) {
      this.departmentService.deletePhoto(this.department.id, photoId).subscribe((response) => {
        location.reload();
      });
    }
  }

  changeMainPhoto(inputElement){
    const file: File = inputElement.files[0];
    if (file.type === 'image/jpeg' || file.type === 'image/png') {
      this.validmainphoto = true;
      const photo = new FormData();
      photo.append('imageFile', file, 'mainPhoto');
      this.departmentService.changeMainPhoto(this.department.id, photo).subscribe((response) => {
        location.reload();
      });
    }
    else{
      this.validmainphoto = false;
    }

  }


  addPhotos(inputElement){
    let count = 0 ;
    const files = new Array<File>();

    for ( let i = 0 ; i < inputElement.files.length ; i = i + 1) {
      files[i] = inputElement.files[i];
      if (inputElement.files[i].type === 'image/jpeg' || inputElement.files[i].type === 'image/png') {
        count = count + 1;
      }
    }

    if (count === inputElement.files.length) {
      this.validphotos = true;
      const photos = new FormData();
      files.forEach((photo) => {
        if (photo) {
          photos.append('imageFile', photo, 'extraPhoto');
        }
      });
      this.departmentService.addPhotos(this.department.id, photos).subscribe((response) => {
          location.reload();
      });
    }
    else {
      this.validphotos = false;
    }

  }


  newTab(photo: Photo){
    const image = new Image();
    if (photo.type === 'image/png') {
      image.src = 'data:image/png;base64,' + photo.photoBytes;
    }
    else if (photo.type === 'image/jpeg') {
      image.src = 'data:image/jpeg;base64,' + photo.photoBytes;
    }

    const w = window.open(  '_blank');
    w.document.write(image.outerHTML);
  }

  hasRole(rolename: string): boolean{
    let flag = false;
    if (this.userDetails) {
      this.userDetails.roles.forEach((role) => {
        if (role === rolename) {
          flag = true;
        }
      });
    }
    return flag;
  }

  findCost(): number {
    let cost = this.department.costPerDay;
    if ( this.params.numberOfChildren || this.params.numberOfAdults) {
      cost = cost + (this.params.numberOfAdults + this.params.numberOfChildren) * this.department.costPerPerson;
    }
    return cost;
  }


   book() {
    let booking = new Booking();
    booking.startDate = this.params.startDate;
    booking.endDate = this.params.endDate;
    booking.numberOfChildren = this.params.numberOfChildren;
    booking.numberOfAdults = + this.params.numberOfAdults;
    this.bookingService.book( this.department.id, booking).subscribe((response) => {
      this.booked = true;
      this.bookModalRef.hide();
      booking = response.body;
      this.router.navigate(['/departments/', booking.department.id.toString()], {queryParams: {bookingId: booking.id}});
    });
  }

   reviewDepartment(){
    if (this.reviewDepText) {
      const review = new Review();
      review.text = this.reviewDepText;
      review.stars = this.starsValue;
      this.reviewService.reviewDepartment(this.department.id, review).subscribe((response) => {
        this.reviewDepText = '';
        this.reviewDepModalRef.hide();
      },
        error => {
        this.errormsg = error.error.message;
        }
      );
    }
   }

  reviewHost(){
    if (this.reviewHostText) {
      const review = new Review();
      review.text = this.reviewHostText;
      this.reviewService.reviewHost(this.department.host.id, review).subscribe((response) => {
        this.reviewHostText = '';
        this.reviewHostModalRef.hide();
      },
        error => {
          this.errormsg = error.error.message;
        }
      );
    }
  }

  messageHost(){
    if (this.messageHostText) {
      const message = new Message();
      message.text = this.messageHostText;
      message.isQuestion = true;
      this.messageService.message(this.department.host.id, this.department.id , null, message).subscribe((response) => {
        this.messageHostText = '';
        this.messageHostModalRef.hide();
      });
    }
  }


}
