import * as dayjs from 'dayjs';
import { IDog } from 'app/entities/dog/dog.model';
import { CustomerType } from 'app/entities/enumerations/customer-type.model';

export interface ICar {
  id?: number;
  name?: string;
  title?: string | null;
  date?: dayjs.Dayjs | null;
  type?: CustomerType | null;
  dog?: IDog | null;
}

export class Car implements ICar {
  constructor(
    public id?: number,
    public name?: string,
    public title?: string | null,
    public date?: dayjs.Dayjs | null,
    public type?: CustomerType | null,
    public dog?: IDog | null
  ) {}
}

export function getCarIdentifier(car: ICar): number | undefined {
  return car.id;
}
