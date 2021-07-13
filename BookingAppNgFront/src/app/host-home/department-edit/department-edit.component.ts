import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Department} from '../../model/department';
import {DepartmentService} from '../../department.service';
import Map from 'ol/Map';
import Tile from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import View from 'ol/View';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import {Icon, Style} from 'ol/style';
import VectorSource from 'ol/source/Vector';
import {Vector} from 'ol/layer';
import {fromLonLat , toLonLat} from 'ol/proj';
import {GeocodingService} from '../../geocoding.service';
import country from 'country-list-js';
import {Photo} from '../../model/photo';
import {User} from '../../model/user';
import {AuthenticationService} from '../../authentication.service';

@Component({
  selector: 'app-department-edit',
  templateUrl: './department-edit.component.html',
  styleUrls: ['./department-edit.component.css']
})
export class DepartmentEditComponent implements OnInit {
  department: Department = new Department();
  loading = false;
  dangerBox = false;
  successBox = false;
  submitattempt = false;
  submiterror: any ;
  submitmsg: string;
  map: Map;
  marker: Feature;

  constructor(private router: Router, private departmentService: DepartmentService, private geocodingService: GeocodingService, private route: ActivatedRoute, private authService: AuthenticationService ) { }


  ngOnInit(): void {

    this.departmentService.getDepartment(this.route.snapshot.paramMap.get('id')).subscribe((department) => {
      this.initializeMap(department);
      Object.assign(this.department , department);
      this.authService.getLoggedInUser().subscribe((userDetails) => {
        if(this.department.host.id !== userDetails.id )
          this.router.navigate(['/hostpage']);
      });

      this.department.mainPhoto = null;
      this.department.photos = null;
      this.department.startDate = new Date(department.startDate);
      this.department.endDate = new Date(department.endDate);
      },
      error => {
        this.router.navigate(['/hostpage']);
      }
    );

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

    this.map.on('click', (event) => {
      const point = <Point> this.marker.getGeometry() ;
      const coords = toLonLat(event.coordinate);
      this.department.longitude = coords[0];
      this.department.latitude = coords[1];
      point.setCoordinates(event.coordinate);
      this.geocodingService.getAddress(this.department.longitude.toString(), this.department.latitude.toString()).subscribe((response) => {
        this.department.address = response.address.Match_addr;
        this.department.country = country.findByIso3(response.address.CountryCode).name;
      });
    });


  }

  setAddress(){
    const addr = this.department.address.replace('&', 'and');
    this.geocodingService.setAddress(addr).subscribe((result) => {
      const point = <Point> this.marker.getGeometry() ;
      this.department.longitude = result.candidates[0].location.x ;
      this.department.latitude  =  result.candidates[0].location.y ;
      this.department.country = country.findByIso3(result.candidates[0].attributes.Country).name;
      point.setCoordinates(fromLonLat([this.department.longitude, this.department.latitude]));
    });
  }

  setDate(value){
    const date = new Date(value);
    if(isNaN(date.getTime()))
      return null;
    else
      return date;

  }


  submit(){

    if ((!this.department.endDate && !this.department.startDate) || (this.department.endDate && this.department.startDate && (this.department.endDate.getTime() >= this.department.startDate.getTime()))) {
      const tmp = this.department.address.split(/[\s,]+/);
      this.department.city = tmp[tmp.length - 1];
      this.loading = true;
      this.departmentService.editDepartment(this.department)
        .subscribe(
          response => {
            this.loading = false;
            this.submitmsg = response.body;
            this.successBox = true;
            this.dangerBox = false;
          },
          error => {
            this.loading = false;
            this.submiterror = error;
            this.dangerBox = true;
            this.successBox = false;
            this.submitattempt = true;
          }
        );
    }
    else{
      this.submitattempt = true;
      this.dangerBox = false;
      this.successBox = false;
    }

  }




}
