import * as dayjs from 'dayjs';
import { IDog } from 'app/entities/dog/dog.model';
import { CustomerType } from 'app/entities/enumerations/customer-type.model';

export interface ICard {
  id?: number;
  name?: string;
  title?: string | null;
  date?: dayjs.Dayjs | null;
  type?: CustomerType | null;
  dog?: IDog | null;
}

export class Card implements ICard {
  constructor(
    public id?: number,
    public name?: string,
    public title?: string | null,
    public date?: dayjs.Dayjs | null,
    public type?: CustomerType | null,
    public dog?: IDog | null
  ) {}
}

export function getCardIdentifier(card: ICard): number | undefined {
  return card.id;
}
