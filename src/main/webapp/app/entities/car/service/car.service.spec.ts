import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { CustomerType } from 'app/entities/enumerations/customer-type.model';
import { ICar, Car } from '../car.model';

import { CarService } from './car.service';

describe('Service Tests', () => {
  describe('Car Service', () => {
    let service: CarService;
    let httpMock: HttpTestingController;
    let elemDefault: ICar;
    let expectedResult: ICar | ICar[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CarService);
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

      it('should create a Car', () => {
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

        service.create(new Car()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Car', () => {
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

      it('should partial update a Car', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
            title: 'BBBBBB',
            date: currentDate.format(DATE_FORMAT),
          },
          new Car()
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

      it('should return a list of Car', () => {
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

      it('should delete a Car', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCarToCollectionIfMissing', () => {
        it('should add a Car to an empty array', () => {
          const car: ICar = { id: 123 };
          expectedResult = service.addCarToCollectionIfMissing([], car);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(car);
        });

        it('should not add a Car to an array that contains it', () => {
          const car: ICar = { id: 123 };
          const carCollection: ICar[] = [
            {
              ...car,
            },
            { id: 456 },
          ];
          expectedResult = service.addCarToCollectionIfMissing(carCollection, car);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Car to an array that doesn't contain it", () => {
          const car: ICar = { id: 123 };
          const carCollection: ICar[] = [{ id: 456 }];
          expectedResult = service.addCarToCollectionIfMissing(carCollection, car);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(car);
        });

        it('should add only unique Car to an array', () => {
          const carArray: ICar[] = [{ id: 123 }, { id: 456 }, { id: 64134 }];
          const carCollection: ICar[] = [{ id: 123 }];
          expectedResult = service.addCarToCollectionIfMissing(carCollection, ...carArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const car: ICar = { id: 123 };
          const car2: ICar = { id: 456 };
          expectedResult = service.addCarToCollectionIfMissing([], car, car2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(car);
          expect(expectedResult).toContain(car2);
        });

        it('should accept null and undefined values', () => {
          const car: ICar = { id: 123 };
          expectedResult = service.addCarToCollectionIfMissing([], null, car, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(car);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
