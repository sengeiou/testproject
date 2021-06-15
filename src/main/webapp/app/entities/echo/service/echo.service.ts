import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IEcho, getEchoIdentifier } from '../echo.model';

export type EntityResponseType = HttpResponse<IEcho>;
export type EntityArrayResponseType = HttpResponse<IEcho[]>;

@Injectable({ providedIn: 'root' })
export class EchoService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/echoes');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/echoes');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(echo: IEcho): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(echo);
    return this.http
      .post<IEcho>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(echo: IEcho): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(echo);
    return this.http
      .put<IEcho>(`${this.resourceUrl}/${getEchoIdentifier(echo) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(echo: IEcho): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(echo);
    return this.http
      .patch<IEcho>(`${this.resourceUrl}/${getEchoIdentifier(echo) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IEcho>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEcho[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEcho[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addEchoToCollectionIfMissing(echoCollection: IEcho[], ...echoesToCheck: (IEcho | null | undefined)[]): IEcho[] {
    const echoes: IEcho[] = echoesToCheck.filter(isPresent);
    if (echoes.length > 0) {
      const echoCollectionIdentifiers = echoCollection.map(echoItem => getEchoIdentifier(echoItem)!);
      const echoesToAdd = echoes.filter(echoItem => {
        const echoIdentifier = getEchoIdentifier(echoItem);
        if (echoIdentifier == null || echoCollectionIdentifiers.includes(echoIdentifier)) {
          return false;
        }
        echoCollectionIdentifiers.push(echoIdentifier);
        return true;
      });
      return [...echoesToAdd, ...echoCollection];
    }
    return echoCollection;
  }

  protected convertDateFromClient(echo: IEcho): IEcho {
    return Object.assign({}, echo, {
      created: echo.created?.isValid() ? echo.created.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.created = res.body.created ? dayjs(res.body.created) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((echo: IEcho) => {
        echo.created = echo.created ? dayjs(echo.created) : undefined;
      });
    }
    return res;
  }
}
