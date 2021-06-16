import { IDog } from 'app/entities/dog/dog.model';
import { IBook } from 'app/entities/book/book.model';
import { IEcho } from 'app/entities/echo/echo.model';
import { AddressType } from 'app/entities/enumerations/address-type.model';

export interface IAuthor {
  id?: number;
  name?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  type?: AddressType | null;
  dog?: IDog | null;
  books?: IBook[] | null;
  echoes?: IEcho[] | null;
}

export class Author implements IAuthor {
  constructor(
    public id?: number,
    public name?: string | null,
    public firstName?: string | null,
    public lastName?: string | null,
    public type?: AddressType | null,
    public dog?: IDog | null,
    public books?: IBook[] | null,
    public echoes?: IEcho[] | null
  ) {}
}

export function getAuthorIdentifier(author: IAuthor): number | undefined {
  return author.id;
}
