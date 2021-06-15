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
import { ICard, getCardIdentifier } from '../card.model';

export type EntityResponseType = HttpResponse<ICard>;
export type EntityArrayResponseType = HttpResponse<ICard[]>;

@Injectable({ providedIn: 'root' })
export class CardService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/cards');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/cards');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(card: ICard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(card);
    return this.http
      .post<ICard>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(card: ICard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(card);
    return this.http
      .put<ICard>(`${this.resourceUrl}/${getCardIdentifier(card) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(card: ICard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(card);
    return this.http
      .patch<ICard>(`${this.resourceUrl}/${getCardIdentifier(card) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICard>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICard[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICard[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addCardToCollectionIfMissing(cardCollection: ICard[], ...cardsToCheck: (ICard | null | undefined)[]): ICard[] {
    const cards: ICard[] = cardsToCheck.filter(isPresent);
    if (cards.length > 0) {
      const cardCollectionIdentifiers = cardCollection.map(cardItem => getCardIdentifier(cardItem)!);
      const cardsToAdd = cards.filter(cardItem => {
        const cardIdentifier = getCardIdentifier(cardItem);
        if (cardIdentifier == null || cardCollectionIdentifiers.includes(cardIdentifier)) {
          return false;
        }
        cardCollectionIdentifiers.push(cardIdentifier);
        return true;
      });
      return [...cardsToAdd, ...cardCollection];
    }
    return cardCollection;
  }

  protected convertDateFromClient(card: ICard): ICard {
    return Object.assign({}, card, {
      date: card.date?.isValid() ? card.date.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? dayjs(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((card: ICard) => {
        card.date = card.date ? dayjs(card.date) : undefined;
      });
    }
    return res;
  }
}
