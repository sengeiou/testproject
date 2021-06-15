import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDog, Dog } from '../dog.model';

import { DogService } from './dog.service';

describe('Service Tests', () => {
  describe('Dog Service', () => {
    let service: DogService;
    let httpMock: HttpTestingController;
    let elemDefault: IDog;
    let expectedResult: IDog | IDog[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(DogService);
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

      it('should create a Dog', () => {
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

        service.create(new Dog()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Dog', () => {
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

      it('should partial update a Dog', () => {
        const patchObject = Object.assign(
          {
            description: 'BBBBBB',
            created: currentDate.format(DATE_FORMAT),
          },
          new Dog()
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

      it('should return a list of Dog', () => {
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

      it('should delete a Dog', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addDogToCollectionIfMissing', () => {
        it('should add a Dog to an empty array', () => {
          const dog: IDog = { id: 123 };
          expectedResult = service.addDogToCollectionIfMissing([], dog);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(dog);
        });

        it('should not add a Dog to an array that contains it', () => {
          const dog: IDog = { id: 123 };
          const dogCollection: IDog[] = [
            {
              ...dog,
            },
            { id: 456 },
          ];
          expectedResult = service.addDogToCollectionIfMissing(dogCollection, dog);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Dog to an array that doesn't contain it", () => {
          const dog: IDog = { id: 123 };
          const dogCollection: IDog[] = [{ id: 456 }];
          expectedResult = service.addDogToCollectionIfMissing(dogCollection, dog);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(dog);
        });

        it('should add only unique Dog to an array', () => {
          const dogArray: IDog[] = [{ id: 123 }, { id: 456 }, { id: 64049 }];
          const dogCollection: IDog[] = [{ id: 123 }];
          expectedResult = service.addDogToCollectionIfMissing(dogCollection, ...dogArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const dog: IDog = { id: 123 };
          const dog2: IDog = { id: 456 };
          expectedResult = service.addDogToCollectionIfMissing([], dog, dog2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(dog);
          expect(expectedResult).toContain(dog2);
        });

        it('should accept null and undefined values', () => {
          const dog: IDog = { id: 123 };
          expectedResult = service.addDogToCollectionIfMissing([], null, dog, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(dog);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
