import * as dayjs from 'dayjs';
import { ICar } from 'app/entities/car/car.model';
import { IAuthor } from 'app/entities/author/author.model';

export interface IDog {
  id?: number;
  name?: string;
  description?: string | null;
  created?: dayjs.Dayjs | null;
  cards?: ICar[] | null;
  author?: IAuthor | null;
}

export class Dog implements IDog {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public created?: dayjs.Dayjs | null,
    public cards?: ICar[] | null,
    public author?: IAuthor | null
  ) {}
}

export function getDogIdentifier(dog: IDog): number | undefined {
  return dog.id;
}
