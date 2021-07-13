import { Component, OnInit } from '@angular/core';
import {DepartmentService} from '../department.service';
import {Department} from '../model/department';
import {DomSanitizer} from '@angular/platform-browser';


@Component({
  selector: 'app-host-home',
  templateUrl: './host-home.component.html',
  styleUrls: ['./host-home.component.css']
})
export class HostHomeComponent implements OnInit {

  departments: Department[] = new Array<Department>();
  page = 1;

  constructor(private departmentService: DepartmentService, private domSanitizer: DomSanitizer) { }

  ngOnInit(): void {

    this.departmentService.getDepartments().subscribe((departments) => {
        Object.assign(this.departments , departments);

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
            if(photo.name === 'mainPhoto')
              department.mainPhoto = photo;
          });
        });
      }
    );

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


}
