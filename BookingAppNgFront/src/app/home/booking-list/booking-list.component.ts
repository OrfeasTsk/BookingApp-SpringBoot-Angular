import { Component, OnInit } from '@angular/core';
import {Booking} from '../../model/booking';
import {BookingService} from '../../booking.service';
import {returnOrUpdate} from 'ol/extent';
import {Router} from '@angular/router';
import {DepartmentService} from '../../department.service';
import {Department} from '../../model/department';

@Component({
  selector: 'app-booking-list',
  templateUrl: './booking-list.component.html',
  styleUrls: ['./booking-list.component.css']
})
export class BookingListComponent implements OnInit {

  bookings: Booking[] = new Array<Booking>();
  page = 1;
  currentDate = new Date();

  constructor(private bookingService: BookingService, private router: Router, private departmentService: DepartmentService) { }

  ngOnInit(): void {

    this.bookingService.getBookings().subscribe((bookings) => {
        Object.assign(this.bookings , bookings);
      }
    );
  }

  findCost(booking): number {
    return  booking.department.costPerDay + (booking.numberOfAdults + booking.numberOfChildren) * booking.department.costPerPerson;
  }

  makeDate(date: Date): Date{
    const newDate = new Date(date);
    newDate.setHours(0,0,0,0);
    return newDate;
  }


  redirectToDetails(booking: Booking){
    /*let params: any = new Object();
    params.startDate = booking.startDate;
    params.endDate = booking.endDate;
    params.numberOfChildren = booking.numberOfChildren;
    params.numberOfAdults =  booking.numberOfAdults;
    params.booked = true;*/
    this.router.navigate(['/departments/', booking.department.id.toString()], {queryParams: {bookingId: booking.id}});
  }



  cancel(booking){
    if(confirm('Do you want to cancel your booking?')) {
      this.bookingService.cancel(booking.id).subscribe((response) => {
        location.reload();
      });
    }

  }

}
