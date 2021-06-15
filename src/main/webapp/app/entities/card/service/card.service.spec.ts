import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { CustomerType } from 'app/entities/enumerations/customer-type.model';
import { ICard, Card } from '../card.model';

import { CardService } from './card.service';

describe('Service Tests', () => {
  describe('Card Service', () => {
    let service: CardService;
    let httpMock: HttpTestingController;
    let elemDefault: ICard;
    let expectedResult: ICard | ICard[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CardService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        title: 'AAAAAAA',
        date: currentDate,
        type: CustomerType.DEFAULT,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            date: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Card', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            date: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.create(new Card()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Card', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            title: 'BBBBBB',
            date: currentDate.format(DATE_FORMAT),
            type: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Card', () => {
        const patchObject = Object.assign(
          {
            title: 'BBBBBB',
            date: currentDate.format(DATE_FORMAT),
          },
          new Card()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Card', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            title: 'BBBBBB',
            date: currentDate.format(DATE_FORMAT),
            type: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Card', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCardToCollectionIfMissing', () => {
        it('should add a Card to an empty array', () => {
          const card: ICard = { id: 123 };
          expectedResult = service.addCardToCollectionIfMissing([], card);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(card);
        });

        it('should not add a Card to an array that contains it', () => {
          const card: ICard = { id: 123 };
          const cardCollection: ICard[] = [
            {
              ...card,
            },
            { id: 456 },
          ];
          expectedResult = service.addCardToCollectionIfMissing(cardCollection, card);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Card to an array that doesn't contain it", () => {
          const card: ICard = { id: 123 };
          const cardCollection: ICard[] = [{ id: 456 }];
          expectedResult = service.addCardToCollectionIfMissing(cardCollection, card);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(card);
        });

        it('should add only unique Card to an array', () => {
          const cardArray: ICard[] = [{ id: 123 }, { id: 456 }, { id: 44318 }];
          const cardCollection: ICard[] = [{ id: 123 }];
          expectedResult = service.addCardToCollectionIfMissing(cardCollection, ...cardArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const card: ICard = { id: 123 };
          const card2: ICard = { id: 456 };
          expectedResult = service.addCardToCollectionIfMissing([], card, card2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(card);
          expect(expectedResult).toContain(card2);
        });

        it('should accept null and undefined values', () => {
          const card: ICard = { id: 123 };
          expectedResult = service.addCardToCollectionIfMissing([], null, card, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(card);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
