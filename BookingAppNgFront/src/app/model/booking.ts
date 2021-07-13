import {Department} from './department';
import {User} from './user';

export class Booking{
  id: number;
  startDate: Date;
  endDate: Date;
  numberOfAdults: number;
  numberOfChildren: number;
  tenant: User;
  department: Department;
}
