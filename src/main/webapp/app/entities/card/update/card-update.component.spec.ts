jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CardService } from '../service/card.service';
import { ICard, Card } from '../card.model';
import { IDog } from 'app/entities/dog/dog.model';
import { DogService } from 'app/entities/dog/service/dog.service';

import { CardUpdateComponent } from './card-update.component';

describe('Component Tests', () => {
  describe('Card Management Update Component', () => {
    let comp: CardUpdateComponent;
    let fixture: ComponentFixture<CardUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let cardService: CardService;
    let dogService: DogService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CardUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CardUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CardUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      cardService = TestBed.inject(CardService);
      dogService = TestBed.inject(DogService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Dog query and add missing value', () => {
        const card: ICard = { id: 456 };
        const dog: IDog = { id: 25730 };
        card.dog = dog;

        const dogCollection: IDog[] = [{ id: 1719 }];
        spyOn(dogService, 'query').and.returnValue(of(new HttpResponse({ body: dogCollection })));
        const additionalDogs = [dog];
        const expectedCollection: IDog[] = [...additionalDogs, ...dogCollection];
        spyOn(dogService, 'addDogToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ card });
        comp.ngOnInit();

        expect(dogService.query).toHaveBeenCalled();
        expect(dogService.addDogToCollectionIfMissing).toHaveBeenCalledWith(dogCollection, ...additionalDogs);
        expect(comp.dogsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const card: ICard = { id: 456 };
        const dog: IDog = { id: 8049 };
        card.dog = dog;

        activatedRoute.data = of({ card });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(card));
        expect(comp.dogsSharedCollection).toContain(dog);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const card = { id: 123 };
        spyOn(cardService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ card });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: card }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(cardService.update).toHaveBeenCalledWith(card);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const card = new Card();
        spyOn(cardService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ card });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: card }));
        saveSubject.complete();

        // THEN
        expect(cardService.create).toHaveBeenCalledWith(card);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const card = { id: 123 };
        spyOn(cardService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ card });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(cardService.update).toHaveBeenCalledWith(card);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackDogById', () => {
        it('Should return tracked Dog primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackDogById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
