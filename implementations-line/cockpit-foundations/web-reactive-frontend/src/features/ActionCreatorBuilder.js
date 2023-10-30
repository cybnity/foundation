/**
 * Generic action creator.
 * @param type Name of the action type to build.
 * @param argNames List of attributes specifying the action payload.
 * @returns {function(...[*]): {type}} A instance of action respecting the prototype.
 */
export function createAction(type, ...argNames) {
    return function (...args) {
        const action = {type}
        argNames.forEach((arg, index) => {
            action[argNames[index]] = args[index]
        })
        return action
    }
}