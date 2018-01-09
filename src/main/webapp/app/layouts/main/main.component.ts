import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRouteSnapshot, NavigationEnd } from '@angular/router';

import { Title } from '@angular/platform-browser';
import {JhiEventManager} from "ng-jhipster";

@Component({
    selector: 'stock-main',
    templateUrl: './main.component.html'
})
export class StockMainComponent implements OnInit {

    constructor(
        private titleService: Title,
        private router: Router,
        private eventManager: JhiEventManager,
    ) {}

    private getPageTitle(routeSnapshot: ActivatedRouteSnapshot) {
        let title: string = (routeSnapshot.data && routeSnapshot.data['pageTitle']) ? routeSnapshot.data['pageTitle'] : 'netasWork2App';
        if (routeSnapshot.firstChild) {
            title = this.getPageTitle(routeSnapshot.firstChild) || title;
        }
        return title;
    }

    ngOnInit() {
        this.router.events.subscribe((event) => {
            if (event instanceof NavigationEnd) {
                this.titleService.setTitle(this.getPageTitle(this.router.routerState.snapshot.root));
            }
        });
        this.eventManager.subscribe("authenticationSuccess",(content)=>{
            console.log(content);
        });
    }
}
