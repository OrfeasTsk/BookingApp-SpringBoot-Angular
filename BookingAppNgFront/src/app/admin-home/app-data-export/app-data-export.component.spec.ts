import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppDataExportComponent } from './app-data-export.component';

describe('AppDataExportComponent', () => {
  let component: AppDataExportComponent;
  let fixture: ComponentFixture<AppDataExportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AppDataExportComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppDataExportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
