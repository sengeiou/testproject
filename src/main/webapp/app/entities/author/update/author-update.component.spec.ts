jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { AuthorService } from '../service/author.service';
import { IAuthor, Author } from '../author.model';
import { IDog } from 'app/entities/dog/dog.model';
import { DogService } from 'app/entities/dog/service/dog.service';
import { IEcho } from 'app/entities/echo/echo.model';
import { EchoService } from 'app/entities/echo/service/echo.service';

import { AuthorUpdateComponent } from './author-update.component';

describe('Component Tests', () => {
  describe('Author Management Update Component', () => {
    let comp: AuthorUpdateComponent;
    let fixture: ComponentFixture<AuthorUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let authorService: AuthorService;
    let dogService: DogService;
    let echoService: EchoService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AuthorUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(AuthorUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AuthorUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      authorService = TestBed.inject(AuthorService);
      dogService = TestBed.inject(DogService);
      echoService = TestBed.inject(EchoService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call dog query and add missing value', () => {
        const author: IAuthor = { id: 456 };
        const dog: IDog = { id: 25730 };
        author.dog = dog;

        const dogCollection: IDog[] = [{ id: 1719 }];
        spyOn(dogService, 'query').and.returnValue(of(new HttpResponse({ body: dogCollection })));
        const expectedCollection: IDog[] = [dog, ...dogCollection];
        spyOn(dogService, 'addDogToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ author });
        comp.ngOnInit();

        expect(dogService.query).toHaveBeenCalled();
        expect(dogService.addDogToCollectionIfMissing).toHaveBeenCalledWith(dogCollection, dog);
        expect(comp.dogsCollection).toEqual(expectedCollection);
      });

      it('Should call Echo query and add missing value', () => {
        const author: IAuthor = { id: 456 };
        const echoes: IEcho[] = [{ id: 72617 }];
        author.echoes = echoes;

        const echoCollection: IEcho[] = [{ id: 52281 }];
        spyOn(echoService, 'query').and.returnValue(of(new HttpResponse({ body: echoCollection })));
        const additionalEchoes = [...echoes];
        const expectedCollection: IEcho[] = [...additionalEchoes, ...echoCollection];
        spyOn(echoService, 'addEchoToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ author });
        comp.ngOnInit();

        expect(echoService.query).toHaveBeenCalled();
        expect(echoService.addEchoToCollectionIfMissing).toHaveBeenCalledWith(echoCollection, ...additionalEchoes);
        expect(comp.echoesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const author: IAuthor = { id: 456 };
        const dog: IDog = { id: 8049 };
        author.dog = dog;
        const echoes: IEcho = { id: 29758 };
        author.echoes = [echoes];

        activatedRoute.data = of({ author });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(author));
        expect(comp.dogsCollection).toContain(dog);
        expect(comp.echoesSharedCollection).toContain(echoes);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const author = { id: 123 };
        spyOn(authorService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ author });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: author }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(authorService.update).toHaveBeenCalledWith(author);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const author = new Author();
        spyOn(authorService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ author });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: author }));
        saveSubject.complete();

        // THEN
        expect(authorService.create).toHaveBeenCalledWith(author);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const author = { id: 123 };
        spyOn(authorService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ author });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(authorService.update).toHaveBeenCalledWith(author);
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

      describe('trackEchoById', () => {
        it('Should return tracked Echo primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackEchoById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedEcho', () => {
        it('Should return option if no Echo is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedEcho(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Echo for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedEcho(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Echo is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedEcho(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
