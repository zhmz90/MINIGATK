module mkbuild

@doc """ copy build file from destine dir to source dir
""" ->
function cpbuild(dst, src)
    build_fl = joinpath(dst, "BUILD")
    @assert isfile(build_fl)
    
    data = readlines(build_fl)
    
    srcname = basename(src)
    data = replace(data, r"name = \"[a-z]+\"", srcname)

    dstbuild = joinpath(src, "BUILD")
    #write(dstbuild, data)
end


end
