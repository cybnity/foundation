import {Component} from 'react';

class CockpitScreen extends Component {
    componentDidMount() {
        // Subscribe to data provider(s) changes
        // store.subscribe(() => this.forceUpdate());
    }

    render() {
        return (
            <>
                <p>visual elements assembled by the display component retained according to the connected user role and
                    to
                    the current Infocon level</p>
            </>
        );
    }
}

export default CockpitScreen;