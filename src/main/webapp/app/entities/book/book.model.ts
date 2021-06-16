import * as dayjs from 'dayjs';
import { IAuthor } from 'app/entities/author/author.model';

export interface IBook {
  id?: number;
  name?: string;
  created?: dayjs.Dayjs | null;
  description?: string | null;
  author?: IAuthor | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public name?: string,
    public created?: dayjs.Dayjs | null,
    public description?: string | null,
    public author?: IAuthor | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
