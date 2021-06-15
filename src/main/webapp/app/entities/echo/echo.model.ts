import * as dayjs from 'dayjs';
import { IActor } from 'app/entities/actor/actor.model';

export interface IEcho {
  id?: number;
  name?: string | null;
  description?: string | null;
  created?: dayjs.Dayjs | null;
  actors?: IActor[] | null;
}

export class Echo implements IEcho {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public created?: dayjs.Dayjs | null,
    public actors?: IActor[] | null
  ) {}
}

export function getEchoIdentifier(echo: IEcho): number | undefined {
  return echo.id;
}
