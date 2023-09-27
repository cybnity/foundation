import {IconContext} from "react-icons";

/**
 * Configuration of icon style reusable as additional icon on perspective title (e.g Tab label).
 * See React-icons library documentation about available reusable icons at https://react-icons.github.io/react-icons
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export default function PerspectiveTitleIconConfig({children}) {

    return (
        <>
            <IconContext.Provider value={{color: "white", size: "1.5em" }}>
                {children}
            </IconContext.Provider>
        </>
    );
}