import { User } from './user';
import {Department} from './department';

export class Message{

  id: number;
  text: string;
  isQuestion: boolean;
  fromUser: User;
  forUser: User;
  aboutDepartment: Department;
  question: Message;
  reply: Message;

}
