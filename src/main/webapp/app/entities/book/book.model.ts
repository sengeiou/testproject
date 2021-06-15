import * as dayjs from 'dayjs';
import { IActor } from 'app/entities/actor/actor.model';

export interface IBook {
  id?: number;
  name?: string;
  created?: dayjs.Dayjs | null;
  description?: string | null;
  actor?: IActor | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public name?: string,
    public created?: dayjs.Dayjs | null,
    public description?: string | null,
    public actor?: IActor | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
