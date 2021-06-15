jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICard, Card } from '../card.model';
import { CardService } from '../service/card.service';

import { CardRoutingResolveService } from './card-routing-resolve.service';

describe('Service Tests', () => {
  describe('Card routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: CardRoutingResolveService;
    let service: CardService;
    let resultCard: ICard | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(CardRoutingResolveService);
      service = TestBed.inject(CardService);
      resultCard = undefined;
    });

    describe('resolve', () => {
      it('should return ICard returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCard = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCard).toEqual({ id: 123 });
      });

      it('should return new ICard if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCard = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultCard).toEqual(new Card());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCard = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCard).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
