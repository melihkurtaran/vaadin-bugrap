import {LitElement, html, customElement, property} from 'lit-element';

@customElement('distribution-bar')
export class DistributionBar extends LitElement {

    @property({type: Number})
    numClosed: Number = 0;
    @property({type: Number})
    numAssigned: Number = 0;
    @property({type: Number})
    numUnassigned: Number = 0;

    @property({type: Number})
    sizeClosed: Number = 0;
    @property({type: Number})
    sizeAssigned: Number = 0;
    @property({type: Number})
    sizeUnassigned: Number = 0;


    render() {
        return html`
        <div style="background-color: #4B5BD6;width:${this.sizeClosed}px;border-radius: 5px 0 0 5px;color:white;min-width: 30px;">
            <b>${this.numClosed}</b>
        </div>
        <div style="background-color: #0A8BAE;width:${this.sizeAssigned}px;color:white;min-width: 30px;">
            <b>${this.numAssigned}</b>
        </div>
        <div style="background-color: #DF9135;width:${this.sizeUnassigned}px;border-radius:0 5px 5px 0;color:white;min-width: 30px;">
            <b>${this.numUnassigned}</b>
        </div>
        
`;
    }


    constructor() {
        super();
    }




}