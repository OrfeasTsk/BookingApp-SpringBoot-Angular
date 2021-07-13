import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DepartmentRegisterComponent } from './department-register.component';

describe('DepartmentRegisterComponent', () => {
  let component: DepartmentRegisterComponent;
  let fixture: ComponentFixture<DepartmentRegisterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DepartmentRegisterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DepartmentRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
