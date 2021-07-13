import {Department} from './department';
import {User} from './user';

export class Review{
  text: string;
  stars: number;
  fromUser: User;
  forUser: User;
  forDepartment: Department;
}
