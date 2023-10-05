/** Definition of actions usable in the cockpit module
 * For more details https://redux.js.org/usage/reducing-boilerplate#actions
 */
// Specification of support action types
import {createAction} from "../ActionCreatorBuilder";
import * as ActionCreatorBuilder from "@reduxjs/toolkit";

// Set of supported referential action types
export const ACTIVATE_PERSPECTIVE = 'ACTIVATE_PERSPECTIVE';
export const CLOSE_PERSPECTIVE = 'CLOSE_PERSPECTIVE';
export const OPEN_PERSPECTIVE = 'OPEN_PERSPECTIVE';

// --- Specification of provided action type respecting mandatory structure ---

/**
 * Action request for a cockpit perspective
 * @param perspectiveId The unique identifier of the perspective to open.
 * @return Action.
 */
export function activatePerspective(perspectiveId) {
    return {
        type: ACTIVATE_PERSPECTIVE,
        perspectiveId
    }
}

/**
 * Action request for a cockpit perspective close.
 * @param id The unique identifier of the perspective to close.
 * @return Action.
 */
export function closePerspective(id) {
    return {
        type: CLOSE_PERSPECTIVE,
        id
    }
}

/**
 * Action request for a cockpit perspective opening in an independent and standalone web browser view.
 * @param id Unique identifier of the resource (e.g information uuid) to manage into new window.
 * @param title Title of the window to open in externalized browser view.
 * @param componentName Name of the reactive component that shall be presented (as presentational component) in the body of the screen to open.
 * @param closable True if the original opener of the externalized screen allow to be closed (e.g via button shown in tab).
 * @param exportable True if a previous cockpit tab shall propose a button allowing to close it.
 * @param imageMode True if none text title shall be shown in original tab but only image (defined by titleImage parameter) shall be shown in tab title area.
 * @param titleImage Reactive component regarding the image to be shown as title of the original tab.
 * @return {{titleImage, exportable, closable, id, componentName, imageMode, type: string, title}}
 */
export function openExternalizedPerspective(id, title, componentName, closable, exportable, imageMode, titleImage) {
    return {
        type: OPEN_PERSPECTIVE,
        id,
        title,
        componentName,
        closable,
        exportable,
        imageMode,
        titleImage
    }
}