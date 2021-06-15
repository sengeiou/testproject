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
import { IDog, getDogIdentifier } from '../dog.model';

export type EntityResponseType = HttpResponse<IDog>;
export type EntityArrayResponseType = HttpResponse<IDog[]>;

@Injectable({ providedIn: 'root' })
export class DogService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/dogs');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/dogs');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(dog: IDog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dog);
    return this.http
      .post<IDog>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(dog: IDog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dog);
    return this.http
      .put<IDog>(`${this.resourceUrl}/${getDogIdentifier(dog) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(dog: IDog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dog);
    return this.http
      .patch<IDog>(`${this.resourceUrl}/${getDogIdentifier(dog) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IDog>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDog[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addDogToCollectionIfMissing(dogCollection: IDog[], ...dogsToCheck: (IDog | null | undefined)[]): IDog[] {
    const dogs: IDog[] = dogsToCheck.filter(isPresent);
    if (dogs.length > 0) {
      const dogCollectionIdentifiers = dogCollection.map(dogItem => getDogIdentifier(dogItem)!);
      const dogsToAdd = dogs.filter(dogItem => {
        const dogIdentifier = getDogIdentifier(dogItem);
        if (dogIdentifier == null || dogCollectionIdentifiers.includes(dogIdentifier)) {
          return false;
        }
        dogCollectionIdentifiers.push(dogIdentifier);
        return true;
      });
      return [...dogsToAdd, ...dogCollection];
    }
    return dogCollection;
  }

  protected convertDateFromClient(dog: IDog): IDog {
    return Object.assign({}, dog, {
      created: dog.created?.isValid() ? dog.created.format(DATE_FORMAT) : undefined,
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
      res.body.forEach((dog: IDog) => {
        dog.created = dog.created ? dayjs(dog.created) : undefined;
      });
    }
    return res;
  }
}
