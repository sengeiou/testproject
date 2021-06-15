import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBook } from '../book.model';
import { BookService } from '../service/book.service';
import { BookDeleteDialogComponent } from '../delete/book-delete-dialog.component';

@Component({
  selector: 'jhi-book',
  templateUrl: './book.component.html',
})
export class BookComponent implements OnInit {
  books?: IBook[];
  isLoading = false;
  currentSearch: string;

  constructor(protected bookService: BookService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.bookService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IBook[]>) => {
            this.isLoading = false;
            this.books = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.bookService.query().subscribe(
      (res: HttpResponse<IBook[]>) => {
        this.isLoading = false;
        this.books = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IBook): number {
    return item.id!;
  }

  delete(book: IBook): void {
    const modalRef = this.modalService.open(BookDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.book = book;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
