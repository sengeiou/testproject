import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IEcho, Echo } from '../echo.model';

import { EchoService } from './echo.service';

describe('Service Tests', () => {
  describe('Echo Service', () => {
    let service: EchoService;
    let httpMock: HttpTestingController;
    let elemDefault: IEcho;
    let expectedResult: IEcho | IEcho[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(EchoService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        description: 'AAAAAAA',
        created: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            created: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Echo', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            created: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            created: currentDate,
          },
          returnedFromService
        );

        service.create(new Echo()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Echo', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            description: 'BBBBBB',
            created: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            created: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Echo', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
            created: currentDate.format(DATE_FORMAT),
          },
          new Echo()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            created: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Echo', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            description: 'BBBBBB',
            created: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            created: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Echo', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addEchoToCollectionIfMissing', () => {
        it('should add a Echo to an empty array', () => {
          const echo: IEcho = { id: 123 };
          expectedResult = service.addEchoToCollectionIfMissing([], echo);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(echo);
        });

        it('should not add a Echo to an array that contains it', () => {
          const echo: IEcho = { id: 123 };
          const echoCollection: IEcho[] = [
            {
              ...echo,
            },
            { id: 456 },
          ];
          expectedResult = service.addEchoToCollectionIfMissing(echoCollection, echo);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Echo to an array that doesn't contain it", () => {
          const echo: IEcho = { id: 123 };
          const echoCollection: IEcho[] = [{ id: 456 }];
          expectedResult = service.addEchoToCollectionIfMissing(echoCollection, echo);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(echo);
        });

        it('should add only unique Echo to an array', () => {
          const echoArray: IEcho[] = [{ id: 123 }, { id: 456 }, { id: 24108 }];
          const echoCollection: IEcho[] = [{ id: 123 }];
          expectedResult = service.addEchoToCollectionIfMissing(echoCollection, ...echoArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const echo: IEcho = { id: 123 };
          const echo2: IEcho = { id: 456 };
          expectedResult = service.addEchoToCollectionIfMissing([], echo, echo2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(echo);
          expect(expectedResult).toContain(echo2);
        });

        it('should accept null and undefined values', () => {
          const echo: IEcho = { id: 123 };
          expectedResult = service.addEchoToCollectionIfMissing([], null, echo, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(echo);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
