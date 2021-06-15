jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BookService } from '../service/book.service';
import { IBook, Book } from '../book.model';

import { BookUpdateComponent } from './book-update.component';

describe('Component Tests', () => {
  describe('Book Management Update Component', () => {
    let comp: BookUpdateComponent;
    let fixture: ComponentFixture<BookUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let bookService: BookService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BookUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BookUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BookUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      bookService = TestBed.inject(BookService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const book: IBook = { id: 456 };

        activatedRoute.data = of({ book });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(book));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const book = { id: 123 };
        spyOn(bookService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ book });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: book }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(bookService.update).toHaveBeenCalledWith(book);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const book = new Book();
        spyOn(bookService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ book });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: book }));
        saveSubject.complete();

        // THEN
        expect(bookService.create).toHaveBeenCalledWith(book);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const book = { id: 123 };
        spyOn(bookService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ book });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(bookService.update).toHaveBeenCalledWith(book);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
