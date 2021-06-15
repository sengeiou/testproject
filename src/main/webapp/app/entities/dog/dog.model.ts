import * as dayjs from 'dayjs';
import { ICard } from 'app/entities/card/card.model';
import { IActor } from 'app/entities/actor/actor.model';

export interface IDog {
  id?: number;
  name?: string | null;
  description?: string | null;
  created?: dayjs.Dayjs | null;
  cards?: ICard[] | null;
  actor?: IActor | null;
}

export class Dog implements IDog {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public created?: dayjs.Dayjs | null,
    public cards?: ICard[] | null,
    public actor?: IActor | null
  ) {}
}

export function getDogIdentifier(dog: IDog): number | undefined {
  return dog.id;
}
