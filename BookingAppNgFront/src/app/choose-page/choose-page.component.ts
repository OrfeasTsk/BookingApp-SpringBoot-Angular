import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-choose-page',
  templateUrl: './choose-page.component.html',
  styleUrls: ['./choose-page.component.css']
})
export class ChoosePageComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

}
