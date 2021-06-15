jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ActorService } from '../service/actor.service';
import { IActor, Actor } from '../actor.model';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IEcho } from 'app/entities/echo/echo.model';
import { EchoService } from 'app/entities/echo/service/echo.service';

import { ActorUpdateComponent } from './actor-update.component';

describe('Component Tests', () => {
  describe('Actor Management Update Component', () => {
    let comp: ActorUpdateComponent;
    let fixture: ComponentFixture<ActorUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let actorService: ActorService;
    let bookService: BookService;
    let echoService: EchoService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ActorUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ActorUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ActorUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      actorService = TestBed.inject(ActorService);
      bookService = TestBed.inject(BookService);
      echoService = TestBed.inject(EchoService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call book query and add missing value', () => {
        const actor: IActor = { id: 456 };
        const book: IBook = { id: 97728 };
        actor.book = book;

        const bookCollection: IBook[] = [{ id: 72788 }];
        spyOn(bookService, 'query').and.returnValue(of(new HttpResponse({ body: bookCollection })));
        const expectedCollection: IBook[] = [book, ...bookCollection];
        spyOn(bookService, 'addBookToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ actor });
        comp.ngOnInit();

        expect(bookService.query).toHaveBeenCalled();
        expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(bookCollection, book);
        expect(comp.booksCollection).toEqual(expectedCollection);
      });

      it('Should call Echo query and add missing value', () => {
        const actor: IActor = { id: 456 };
        const echoes: IEcho[] = [{ id: 72617 }];
        actor.echoes = echoes;

        const echoCollection: IEcho[] = [{ id: 52281 }];
        spyOn(echoService, 'query').and.returnValue(of(new HttpResponse({ body: echoCollection })));
        const additionalEchoes = [...echoes];
        const expectedCollection: IEcho[] = [...additionalEchoes, ...echoCollection];
        spyOn(echoService, 'addEchoToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ actor });
        comp.ngOnInit();

        expect(echoService.query).toHaveBeenCalled();
        expect(echoService.addEchoToCollectionIfMissing).toHaveBeenCalledWith(echoCollection, ...additionalEchoes);
        expect(comp.echoesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const actor: IActor = { id: 456 };
        const book: IBook = { id: 73421 };
        actor.book = book;
        const echoes: IEcho = { id: 29758 };
        actor.echoes = [echoes];

        activatedRoute.data = of({ actor });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(actor));
        expect(comp.booksCollection).toContain(book);
        expect(comp.echoesSharedCollection).toContain(echoes);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const actor = { id: 123 };
        spyOn(actorService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ actor });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: actor }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(actorService.update).toHaveBeenCalledWith(actor);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const actor = new Actor();
        spyOn(actorService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ actor });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: actor }));
        saveSubject.complete();

        // THEN
        expect(actorService.create).toHaveBeenCalledWith(actor);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const actor = { id: 123 };
        spyOn(actorService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ actor });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(actorService.update).toHaveBeenCalledWith(actor);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackBookById', () => {
        it('Should return tracked Book primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackBookById(0, entity);
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
