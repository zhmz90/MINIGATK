module Rewrite

function rename_package!(filepath, newname)
    lines = readlines(filepath)
    for (ind,line) in emumerate(lines)
        if line[1:7] = "package"
            lines[ind] = string("package"," ", newname)
        end
    end
    
    open(filepath,"w") do file
        write(file, lines)
    end

    Union{}
end

function remove_comment!(filepath)
    lines = readlines(filepath)
    st = ed = 0
    state = false
    for (ind, line) in emumerate(lines)
        if line[1:2] = "/*"
            st = ind
            state = true
        elseif st != 0 && line[1:2] = "* "
            @assert state == true
        elseif line[1:2] == "*/"
            @assert state == true
            ed = ind
        end
    end
    lines = vcat(lines[1:st-1],lines[ed+1:end])
    write(filepath, lines)
end


end
