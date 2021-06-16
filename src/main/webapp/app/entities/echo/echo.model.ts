import * as dayjs from 'dayjs';
import { IAuthor } from 'app/entities/author/author.model';

export interface IEcho {
  id?: number;
  name?: string;
  description?: string | null;
  created?: dayjs.Dayjs | null;
  authors?: IAuthor[] | null;
}

export class Echo implements IEcho {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public created?: dayjs.Dayjs | null,
    public authors?: IAuthor[] | null
  ) {}
}

export function getEchoIdentifier(echo: IEcho): number | undefined {
  return echo.id;
}
