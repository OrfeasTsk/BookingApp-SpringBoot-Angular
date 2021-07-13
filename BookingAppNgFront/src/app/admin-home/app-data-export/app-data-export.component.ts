import { Component, OnInit } from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {DepartmentService} from '../../department.service';
import {BookingService} from '../../booking.service';
import {ReviewService} from '../../review.service';

@Component({
  selector: 'app-app-data-export',
  templateUrl: './app-data-export.component.html',
  styleUrls: ['./app-data-export.component.css']
})
export class AppDataExportComponent implements OnInit {


  constructor(private domSanitizer: DomSanitizer, private departmentService: DepartmentService, private bookingService: BookingService, private reviewService: ReviewService) { }


  ngOnInit(): void {
  }

  downloadDepartments(type: string, element){
   this.departmentService.getDepartmentsForExport(type).subscribe((response) => {
    element.setAttribute('href', 'data:text/' + type + ';charset=UTF-8,' + encodeURIComponent(JSON.stringify(response)));
    element.setAttribute('download', 'departments.' + type);
    element.click();
   });
  }


  downloadBookings(type: string, element){
    this.bookingService.getBookingsForExport(type).subscribe((response) => {
      element.setAttribute('href', 'data:text/' + type + ';charset=UTF-8,' + encodeURIComponent(JSON.stringify(response)));
      element.setAttribute('download', 'bookings.' + type);
      element.click();
    });
  }

  downloadDepartmentReviews(type: string, element){
    this.reviewService.getDepartmentReviewsForExport(type).subscribe((response) => {
      element.setAttribute('href', 'data:text/' + type + ';charset=UTF-8,' + encodeURIComponent(JSON.stringify(response)));
      element.setAttribute('download', 'departmentreviews.' + type);
      element.click();
    });
  }

  downloadHostReviews(type: string, element){
    this.reviewService.getHostReviewsForExport(type).subscribe((response) => {
      element.setAttribute('href', 'data:text/' + type + ';charset=UTF-8,' + encodeURIComponent(JSON.stringify(response)));
      element.setAttribute('download', 'hostreviews.' + type);
      element.click();
    });
  }



}
