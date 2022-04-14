import {LitElement, html, customElement, property} from 'lit-element';

@customElement('distribution-bar')
export class DistributionBar extends LitElement {

    @property({type: Number})
    numClosed: Number = 0;
    @property({type: Number})
    numAssigned: Number = 0;
    @property({type: Number})
    numUnassigned: Number = 0;

    render() {
        return html`
        <div style="background-color: #4B5BD6;width:${this.calculateSize(this.numClosed.valueOf())}px;border-radius: 5px 0 0 5px;color:white;min-width: 30px;">
            <b>${this.numClosed}</b>
        </div>
        <div style="background-color: #0A8BAE;width:${this.calculateSize(this.numAssigned.valueOf())}px;color:white;min-width: 30px;">
            <b>${this.numAssigned}</b>
        </div>
        <div style="background-color: #DF9135;width:${this.calculateSize(this.numUnassigned.valueOf())}px;border-radius:0 5px 5px 0;color:white;min-width: 30px;">
            <b>${this.numUnassigned}</b>
        </div>
        
`;
    }

    calculateSize(num: number): number {
        return num * 30;
    }

    constructor() {
        super();
    }

}