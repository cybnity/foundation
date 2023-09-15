class CockpitScreen extends React.Component {
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
