import { IBook } from 'app/entities/book/book.model';
import { IDog } from 'app/entities/dog/dog.model';
import { IEcho } from 'app/entities/echo/echo.model';
import { AddressType } from 'app/entities/enumerations/address-type.model';

export interface IActor {
  id?: number;
  name?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  type?: AddressType | null;
  book?: IBook | null;
  dogs?: IDog[] | null;
  echoes?: IEcho[] | null;
}

export class Actor implements IActor {
  constructor(
    public id?: number,
    public name?: string | null,
    public firstName?: string | null,
    public lastName?: string | null,
    public type?: AddressType | null,
    public book?: IBook | null,
    public dogs?: IDog[] | null,
    public echoes?: IEcho[] | null
  ) {}
}

export function getActorIdentifier(actor: IActor): number | undefined {
  return actor.id;
}
