import {Role} from './role';
import {Department} from './department';
import {Photo} from './photo';
import {Booking} from './booking';
import {Review} from './review';
import {Message} from './message';

export class User {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    password: string;
    passwordConfirm: string;
    email: string;
    phone: string;
    roles: Array<Role> = new Array<Role>();
    profilePhoto: Photo;
    departments: Array<Department> = new Array<Department>();
    bookings: Array<Booking> = new Array<Booking>();
    messagesForUsers: Array<Message> = new Array<Message>();
    messagesFromUsers: Array<Message> = new Array<Message>();
    reviewsForUsers: Array<Review> = new Array<Review>();
    reviewsFromUsers: Array<Review> = new Array<Review>();
    accepted: boolean;

}
