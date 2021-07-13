import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
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

@Component({
  selector: 'app-department-register',
  templateUrl: './department-register.component.html',
  styleUrls: ['./department-register.component.css']
})
export class DepartmentRegisterComponent implements OnInit {
  department: Department = new Department();
  loading = false;
  dangerBox = false;
  successBox = false;
  submitattempt = false;
  submiterror: any ;
  submitmsg: string;
  mainPhoto: File;
  photos = new Array<File>();
  validphotos = true;
  map: Map;
  marker: Feature;

  constructor(private router: Router, private departmentService: DepartmentService, private geocodingService: GeocodingService ) { }

  ngOnInit(){
    this.initializeMap();
    this.department.maxPeople = 1;
    this.department.minBookingDays = 1;
    this.department.numberOfBaths = 0;
    this.department.numberOfBedrooms = 0;
    this.department.numberOfBeds = 0;
    this.department.hasAirCondition = false;
    this.department.hasElevator = false;
    this.department.hasHeat = false;
    this.department.hasInternet = false;
    this.department.hasKitchen = false;
    this.department.hasLivingRoom = false;
    this.department.hasParking = false;
    this.department.hasTv = false;
    this.department.smokingAllowed = false;
    this.department.eventsAllowed = false;
    this.department.petsAllowed = false;
    this.department.type = null;

  }

  setMainPhoto(inputElement){
    this.mainPhoto = inputElement.files[0];
  }
  setPhotos(inputElement){
    let count = 0 ;

    for ( let i = 0 ; i < inputElement.files.length ; i = i + 1) {
      this.photos[i] = inputElement.files[i];
      if (this.photos[i].type === 'image/jpeg' || this.photos[i].type === 'image/png') {
        count = count + 1;
      }
    }

    if (count === inputElement.files.length) {
      this.validphotos = true;
    }
    else {
      this.validphotos = false;
    }
  }

  initializeMap(){
    this.marker = new Feature({
      geometry: new Point(fromLonLat([Number.MAX_VALUE, Number.MAX_VALUE]))
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


  submit(depregform){

    if (depregform.form.valid && this.department.startDate && this.department.endDate && ( this.department.endDate.getTime() >= this.department.startDate.getTime()) && this.validphotos === true && this.mainPhoto && (this.mainPhoto.type === 'image/jpeg' || this.mainPhoto.type === 'image/png')) {
      const tmp = this.department.address.split(/[\s,]+/);
      this.department.city = tmp[tmp.length - 1];
      const formWrapper = new FormData();
      const departmentBlob = new Blob([JSON.stringify(this.department)], { type: 'application/json'});
      if (this.mainPhoto) {
        formWrapper.append('imageFile' , this.mainPhoto , 'mainPhoto');
      }
      this.photos.forEach((photo) => {
        if (photo) {
          formWrapper.append('imageFile', photo, 'extraPhoto');
        }
      });

      formWrapper.append('object', departmentBlob );
      this.loading = true;
      this.departmentService.addDepartment(formWrapper)
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
